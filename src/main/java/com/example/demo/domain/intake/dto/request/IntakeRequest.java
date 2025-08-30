package com.example.demo.domain.intake.dto.request;

import java.sql.Time;

public record IntakeRequest (
        String name,
        Integer count,

        Integer capacity,

        Integer caffeine_mg,

        Time intake_time
){
}
