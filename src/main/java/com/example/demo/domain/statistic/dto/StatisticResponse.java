package com.example.demo.domain.statistic.dto;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalTime;
import java.util.List;

public record StatisticResponse (
    Integer dailyIntake,
    LocalTime lastIntakeTime,
    Integer avgIntake,
    List<WeeklyIntakeDto> weeklyIntakeChart
){
    public record WeeklyIntakeDto(
            String day, // 요일
            Integer caffeine // 해당 요일 섭취량
    ){}
}
