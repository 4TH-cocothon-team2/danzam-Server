package com.app.intake.dto.request;

import java.sql.Time;
import java.time.LocalDateTime;

public record IntakeRequest (
        String name,
        Integer count,

        Integer capacity,

        Integer caffeine_mg,

        LocalDateTime intakeAt
){
}
