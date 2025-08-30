package com.example.demo.domain.intake.dto;

import com.example.demo.domain.intake.entity.IntakeRecord;

import java.sql.Time;

public record IntakeDto (
        String name,
        Integer count,
        Integer capacity,
        Integer caffeine_mg,
        Time intake_time
){
    //IntakeRecord 엔티티를 IntakeDto로 변환하는 생성자
    public IntakeDto(IntakeRecord intakeRecord) {
        this(
                intakeRecord.getIntakeId(),
                intakeRecord.getCount(),
                intakeRecord.getCapacity(),
                intakeRecord.getCaffeine_mg(),
                intakeRecord.getIntake_time()
        );
    }
}
