package com.app.sleep.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SleepPageSummaryResponse {
    private LocalDate targetDate;
    private LocalDateTime sleepAt;
    private LocalDateTime wakeAt;

    private Integer totalCaffeineMg;      // 하루 총 섭취량
    private LocalDateTime lastIntakeAt;   // 하루 마지막 음용 시각

    private Double preSleepResidualMg;    // 수면 전 잔존량
    private LocalDateTime thresholdDropAt; // mg<=threshold 최초 시각(참고용)

    private Integer sleepQualityScore;
}
