package com.app.statistic.service;

import com.app.intake.entity.Intake;
import com.app.intake.repository.IntakeRepository;
import com.app.statistic.dto.StatisticResponse;
import com.app.user.entity.User;
import com.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticService {
    private final UserRepository userRepository;
    private final IntakeRepository intakeRepository;

    public StatisticResponse getMyStatistics(String uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);

        // 1. 오늘의 섭취 기록 조회
        List<Intake> todayIntakes = intakeRepository.findByUserAndIntakeAtBetween(user, startOfToday, endOfToday);

        //2. 일일 섭취량 계산
        int dailyIntake = todayIntakes.stream()
                .mapToInt(Intake::getCaffeine_mg)
                .sum();
        //3. 최종 음용 시간 계산
        LocalTime lastIntakeTime = todayIntakes.stream()
                .map(intake -> intake.getIntakeAt().toLocalTime())
                .max(Comparator.naturalOrder())
                .orElse(null);// 오늘 마시지 않았다면 null

        //4. 최근 7일간의 기록 조회
        LocalDateTime startOf7Days = today.minusDays(6).atStartOfDay();
        List<Intake> last7DaysIntakes = intakeRepository.findByUserAndIntakeAtBetween(user, startOf7Days, endOfToday);

        //5. 4번 합을 구해서 7로 나눈다 그럼 평균 계산됨
        int totalCaffeineLast7days = last7DaysIntakes.stream()
                .mapToInt(Intake::getCaffeine_mg)
                .sum();
        int avgIntake = totalCaffeineLast7days/7;

        List<StatisticResponse.WeeklyIntakeDto> weeklyChart = createWeeklyChart(user, today);
        return  new StatisticResponse(dailyIntake, lastIntakeTime, avgIntake, weeklyChart );
    }

    //**주간 카페인 섭취량 차트 데이터 생성**
    private List<StatisticResponse.WeeklyIntakeDto> createWeeklyChart(User user, LocalDate today) {
        // 1. 이번 주 월요일 날짜 계산
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDateTime endOfWeek = today.atTime(LocalTime.MAX);

        // 2. 이번 주 월요일부터 오늘까지의 모든 섭취 기록을 DB에서 가져옴
        List<Intake> weeklyIntakes = intakeRepository.findByUserAndIntakeAtBetween(user, startOfWeek.atStartOfDay(), endOfWeek);

        // 3. 요일별 카페인 합계를 저장할 Map 생성 (모든 요일을 0으로 초기화)
        Map<DayOfWeek, Integer> dailyCaffeineMap = new java.util.LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            dailyCaffeineMap.put(day, 0);
        }

        // 4. 가져온 기록들을 날짜별로 합산
        for (Intake intake : weeklyIntakes) {
            DayOfWeek dayOfWeek = intake.getIntakeAt().getDayOfWeek();
            int currentCaffeine = dailyCaffeineMap.get(dayOfWeek);
            dailyCaffeineMap.put(dayOfWeek, currentCaffeine + intake.getCaffeine_mg());
        }

        // 5. 차트에 맞는 DTO 리스트로 변환 (월요일부터 일요일 순서로)
        List<StatisticResponse.WeeklyIntakeDto> weeklyChart = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            // 월요일부터 오늘 요일까지만 리스트에 추가
            if (day.getValue() > today.getDayOfWeek().getValue() && startOfWeek.isBefore(today)) {
                break;
            }
            weeklyChart.add(new StatisticResponse.WeeklyIntakeDto(
                    getDayOfWeekKorean(day),
                    dailyCaffeineMap.get(day)
            ));
            if (day == DayOfWeek.SUNDAY) break; // 일요일까지만
        }

        return weeklyChart;
    }

    // DayOfWeek를 한글 요일로 변환하는 헬퍼 메서드
    private String getDayOfWeekKorean(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }
}
