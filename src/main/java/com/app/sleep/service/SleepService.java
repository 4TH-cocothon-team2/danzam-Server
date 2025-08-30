package com.app.sleep.service;


import com.app.analysis.Intake;
import com.app.analysis.User;
import com.app.analysis.UserRepository;
import com.app.sleep.dto.SleepLogRequest;
import com.app.sleep.dto.SleepPageSummaryResponse;
import com.app.sleep.dto.SleepQualityRequest;
import com.app.sleep.entity.SleepLog;
import com.app.sleep.entity.SleepQuality;
import com.app.sleep.repository.SleepLogRepository;
import com.app.sleep.repository.SleepQualityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SleepService {

    private final UserRepository userRepository;
    private final SleepLogRepository sleepLogRepository;
    private final SleepQualityRepository sleepQualityRepository;
    private final IntakeRepository intakeRepository;

    // ====== 수면 기록/품질 upsert ======
    @Transactional
    public SleepLog upsertSleepLog(SleepLogRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.getUserId()));

        LocalDate target = (req.getTargetDate() != null)
                ? req.getTargetDate()
                : (req.getSleepAt() != null ? req.getSleepAt().toLocalDate() : LocalDate.now());

        SleepLog log = sleepLogRepository.findByUserAndTargetDate(user, target)
                .orElse(SleepLog.builder().user(user).targetDate(target).build());

        log.setSleepAt(req.getSleepAt());
        log.setWakeAt(req.getWakeAt());
        return sleepLogRepository.save(log);
    }

    @Transactional
    public SleepQuality upsertSleepQuality(SleepQualityRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.getUserId()));

        SleepQuality quality = sleepQualityRepository.findByUserAndTargetDate(user, req.getTargetDate())
                .orElse(SleepQuality.builder().user(user).targetDate(req.getTargetDate()).build());

        quality.setScore(req.getScore());
        return sleepQualityRepository.save(quality);
    }

    // ====== 페이지 요약 ======
    @Transactional(readOnly = true)
    public SleepPageSummaryResponse getSummary(String userId, LocalDate targetDate, Double thresholdMgOrNull) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Optional<SleepLog> logOpt = sleepLogRepository.findByUserAndTargetDate(user, targetDate);
        Optional<SleepQuality> qualityOpt = sleepQualityRepository.findByUserAndTargetDate(user, targetDate);

        // (1) 하루 총 섭취량/마지막 음용시각
        DaySum day = summarizeDay(user, targetDate);

        // (2) 수면 전 잔존량 규칙
        Double preSleepResidual = null;
        LocalDateTime dropAt = null;
        if (logOpt.isPresent() && logOpt.get().getSleepAt() != null) {
            LocalDateTime sleepAt = logOpt.get().getSleepAt();
            double threshold = (thresholdMgOrNull != null) ? thresholdMgOrNull : 5.0;

            dropAt = thresholdDropTime(user, targetDate, threshold).orElse(null);

            if (dropAt != null && !sleepAt.isBefore(dropAt)) {
                // 수면시각 >= 임계 하강 시각 → 0 mg
                preSleepResidual = 0.0;
            } else {
                preSleepResidual = residualAt(user, sleepAt);
            }
        }

        return SleepPageSummaryResponse.builder()
                .targetDate(targetDate)
                .sleepAt(logOpt.map(SleepLog::getSleepAt).orElse(null))
                .wakeAt(logOpt.map(SleepLog::getWakeAt).orElse(null))
                .totalCaffeineMg(day.totalMg)
                .lastIntakeAt(day.lastIntakeAt)
                .preSleepResidualMg(preSleepResidual)
                .thresholdDropAt(dropAt)
                .sleepQualityScore(qualityOpt.map(SleepQuality::getScore).orElse(null))
                .build();
    }

    // ====== 아래는 CaffeineService에서 하던 일을 인라인 ======

    // 기본 반감기(시간) – 필요 시 환경변수/사용자 프로필로 바꿀 수 있음
    private static final double HALF_LIFE_HOURS = 5.0;
    private static final double LN2 = Math.log(2.0);

    /** t 시점 잔존량(단순 반감기 합성) */
    private double residualAt(User user, LocalDateTime t) {
        LocalDateTime from = t.minusHours(48); // 48h 이내만 영향 고려
        List<Intake> intakes = intakeRepository.findByUserAndIntakeAtBetween(user, from, t);

        double k = LN2 / HALF_LIFE_HOURS;
        double mg = 0.0;
        for (Intake i : intakes) {
            if (i.getIntakeAt().isAfter(t)) continue;
            double hours = Duration.between(i.getIntakeAt(), t).toMinutes() / 60.0;
            // 필드명이 caffeine_mg이면 getCaffeine_mg()로 바꾸기
            mg += i.getCaffeine_mg() * Math.exp(-k * hours);
        }
        return mg;
    }

    /** targetDate 00:00 ~ +48h에서 mg ≤ threshold 가 되는 최초 시각(10분 단위 스캔) */
    private Optional<LocalDateTime> thresholdDropTime(User user, LocalDate targetDate, double thresholdMg) {
        LocalDateTime cur = targetDate.atStartOfDay();
        LocalDateTime end = cur.plusDays(2);
        while (!cur.isAfter(end)) {
            if (residualAt(user, cur) <= thresholdMg) return Optional.of(cur);
            cur = cur.plusMinutes(10);
        }
        return Optional.empty();
    }

    /** 하루 총 섭취량과 마지막 음용 시각 */
    private DaySum summarizeDay(User user, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        List<Intake> list = intakeRepository.findByUserAndIntakeAtBetween(user, start, end);

        int total = list.stream()
                // 필드명이 caffeine_mg이면 getCaffeine_mg()
                .mapToInt(Intake::getCaffeineMg)
                .sum();

        LocalDateTime last = list.stream()
                .max(Comparator.comparing(Intake::getIntakeAt))
                .map(Intake::getIntakeAt)
                .orElse(null);

        return new DaySum(total, last);
    }

    private record DaySum(int totalMg, LocalDateTime lastIntakeAt) {}
}
}
