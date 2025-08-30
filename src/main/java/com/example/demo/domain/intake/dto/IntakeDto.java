package com.example.demo.domain.intake.dto;

import com.example.demo.domain.intake.entity.Intake;

import java.time.LocalDateTime;

public record IntakeDto (
        Long intakeId,
        String name,
        Integer count,
        Integer capacity,
        Integer caffeine_mg,
        LocalDateTime intakeAt
){
    //IntakeRecord 엔티티를 IntakeDto로 변환하는 생성자
    public IntakeDto (Intake intake) {
        this(
                intake.getIntakeId(),
                intake.getName(),
                intake.getCount(),
                intake.getCapacity(),
                intake.getCaffeine_mg(),
                intake.getIntakeAt()
        );
    }
}
