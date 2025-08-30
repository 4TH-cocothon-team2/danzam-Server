package com.app.sleep.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SleepLogRequest {
    private String userId;
    private LocalDateTime sleepAt;
    private LocalDateTime wakeAt;
    private LocalDate targetDate;
}
