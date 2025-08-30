package com.app.analysis;

import com.app.analysis.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 메인 페이지 분석용 서비스
 * 역할:
 *  1) DB에서 사용자 정보(UserProfile)와 당일 섭취 기록(Intake) 조회
 *  2) GPT에게 계산을 맡겨 JSON 결과를 받음 (성공 시 바로 반환)
 *  3) GPT 실패/타임아웃 시, 동일 지표를 로컬(폴백) 수식으로 계산하여 반환
 *
 * 반환 DTO(AnalysisResponse)는 프론트 도넛/잔존시간/예상 수면시간/시계열에 바로 사용 가능.
 */

@Service
@RequiredArgsConstructor
public class AnalysisService {

    // JPA 레포: 사용자/섭취 기록 조회
    private final UserProfileRepository userRepo;
    private final IntakeRepository intakeRepo;

    // OpenAI 호출 서비스 (JSON만 반환하도록 프롬프트)
    private final GptCalculatorService gpt;

    public AnalysisResponse buildDailyAnalysis(LocalDate dateOpt, String userPk) {
        //날짜 기본값 처리
        LocalDate date = (dateOpt == null) ? LocalDate.now() : dateOpt;

        // 사용자 정보 로드 (없으면 400 에러)
        UserProfile user = userRepo.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("userPk not found"));

        //당일 00:00 - 익일 00:00 구간 섭취 기록 로드
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<Intake> intakes = intakeRepo.findByUserPkAndTakenAtBetween(userPk, start, end);

        // 1) GPT로 계산 시도
        String json = gpt.computeByGpt(user, intakes, date);
        if (json != null) {
            return parseGptJson(json);
        }

        // 2) 실패 시 로컬 폴백(기본 반감기 5h, 보정 포함)
        return fallbackLocalCalc(user, intakes, date);
    }

    private AnalysisResponse parseGptJson(String json) {
        JSONObject o = new JSONObject(json);
        double remainingPercent = o.optDouble("remainingPercent", 0);
        double remainingMg = o.optDouble("remainingMg", 0);
        double hoursToZero = o.optDouble("hoursToZero", 0);

        //ISO 문자열 -> LocalDateTime
        LocalDateTime estimated = LocalDateTime.parse(o.getString("estimatedSleepTime"));

        // 시계열 파싱
        List<AnalysisResponse.Point> series = new ArrayList<>();
        var arr = o.optJSONArray("series");
        if (arr != null) {
            for (int i=0;i<arr.length();i++){
                JSONObject p = arr.getJSONObject(i);
                series.add(new AnalysisResponse.Point(
                        LocalDateTime.parse(p.getString("time")),
                        p.getDouble("mg")
                ));
            }
        }

        return new AnalysisResponse(remainingPercent, remainingMg, hoursToZero, estimated, series);
    }

    // --- 폴백 계산 ---
    /**
     * 기본 반감기 5h에서 사용자 특성(흡연/임신/약물)으로 보정 → 시계열을 30분 간격으로 생성
     *  - 현재 잔존량(current)
     *  - 5mg 이하 최초 시각(zeroTime) = 예상 수면 시간
     *  - now→zeroTime 시간(hoursToZero)
     *  - 총 섭취 대비 현재 비율(remainingPercent)
     *  - 간단 점수 기반 코멘트(comment)
     */

    private static final int STEP_MINUTES = 10;   // 1, 5, 10 등 네가 원하는 간격
    private static final double THRESHOLD_MG = 5.0;

    private AnalysisResponse fallbackLocalCalc(UserProfile u, List<Intake> xs, LocalDate date) {
        double half = adjustedHalfLife(u);
        LocalDateTime now = LocalDateTime.now();

        if (xs == null || xs.isEmpty()) {
            return new AnalysisResponse(0, 0, 0, now, List.of());
        }

        // ✅ '현재 이전(<= now)' 중 가장 늦은 섭취시각을 시리즈 시작으로
        Optional<LocalDateTime> lastPastIntakeOpt = xs.stream()
                .map(Intake::getTakenAt)
                .filter(t -> !t.isAfter(now))           // now 이전(같음 포함)만
                .max(Comparator.naturalOrder());

        LocalDateTime from = lastPastIntakeOpt.orElse(now);
        // 2) 과도한 길이 방지(최대 48시간)
        LocalDateTime hardEnd = from.plusHours(48);

        // 3) 시리즈 생성 (마지막 섭취 시각부터!)
        List<AnalysisResponse.Point> series = new ArrayList<>();
        for (LocalDateTime t = from; !t.isAfter(hardEnd); t = t.plusMinutes(STEP_MINUTES)) {
            double mg = 0.0;
            for (Intake in : xs) {
                mg += decay(in.getMg(), in.getTakenAt(), t, half);
            }
            series.add(new AnalysisResponse.Point(t, round1(mg)));
        }


        // now 시점 값 계산
        double nowMg = round1(xs.stream()
                .mapToDouble(in -> decay(in.getMg(), in.getTakenAt(), now, half))
                .sum());

        // 남은 퍼센트 / 시간 / 수면 추정
        double total = xs.stream().mapToDouble(Intake::getMg).sum();
        double percent = (total <= 0) ? 0 : round1(nowMg / total * 100.0);

        // 5mg 이하 최초 시간(현재 이후)을 zeroTime으로
        LocalDateTime zeroTime = (nowMg <= THRESHOLD_MG) ? now
                : series.stream()
                        .filter(p -> !p.time().isBefore(now)) // now 이후만
                        .filter(p -> p.mg() <= THRESHOLD_MG)
                        .map(AnalysisResponse.Point::time)
                        .findFirst()
                        .orElse(hardEnd);

        double hoursToZero = Math.max(0, Duration.between(now, zeroTime).toMinutes() / 60.0);

        // --- ① tail 자르기: zeroTime(또는 최초 5mg 이하) 이후는 버림 ---
        int cutIdx = -1;
        for (int i = 0; i < series.size(); i++) {
            // now 이후 구간에서 처음으로 5mg 이하가 되는 지점
            if (!series.get(i).time().isBefore(now) && series.get(i).mg() <= THRESHOLD_MG) {
                cutIdx = i; break;
            }
        }
        if (cutIdx >= 0 && cutIdx + 1 < series.size()) {
            series = new ArrayList<>(series.subList(0, cutIdx + 1));
        }

        // --- ② now 포인트 삽입(시간 순서 유지) ---
        // 10분 격자와 now가 정확히 일치하지 않으면, 시리즈 안의 올바른 위치에 삽입/치환
        int pos = Collections.binarySearch(
                series,
                new AnalysisResponse.Point(now, 0),
                Comparator.comparing(AnalysisResponse.Point::time)
        );
        if (pos >= 0) {
            // 동일 시각 격자점이 있다면 값을 nowMg로 갱신
            series.set(pos, new AnalysisResponse.Point(now, nowMg));
        } else {
            int insertAt = -pos - 1;
            insertAt = Math.max(0, Math.min(insertAt, series.size()));
            // 시간 순서 유지하며 삽입 (라인 차트가 뒤로 꺾이지 않도록)
            series.add(insertAt, new AnalysisResponse.Point(now, nowMg));
        }

        // 응답
        return new AnalysisResponse(
                percent,
                nowMg,
                hoursToZero,
                zeroTime,
                series
        );
    }

    // 사용자 특성 기반 반감기 보정(시간 단위)
    private static double adjustedHalfLife(UserProfile u) {
        double half = 5.0;
        if (Boolean.TRUE.equals(u.getSmoker())) half /= 1.5;
        if (Boolean.TRUE.equals(u.getPregnant())) half *= 2.0;
        if (Boolean.TRUE.equals(u.getMeds())) half *= 1.3;
        return half;
    }
    // 반감기 기반 잔존량 계산: t < 섭취시각이면 0, 그 외  exp(-k*Δh)
    private static double decay(double dose, LocalDateTime at, LocalDateTime t, double half) {
        if (t.isBefore(at)) return 0.0;
        double h = Duration.between(at, t).toMinutes()/60.0;
        double k = Math.log(2)/half;
        return dose * Math.exp(-k*h);
    }

    // 소수 1자리 반올림
    private static double round1(double v){ return Math.round(v*10)/10.0; }


}
