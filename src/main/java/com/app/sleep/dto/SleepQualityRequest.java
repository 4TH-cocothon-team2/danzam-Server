package com.app.sleep.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SleepQualityRequest {
    private String userId;
    private LocalDate targetDate; // SleepLog와 동일 기준
    private Integer score;
}
