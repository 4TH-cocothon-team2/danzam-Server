package com.app.sleep.controller;

import com.app.sleep.dto.SleepLogRequest;
import com.app.sleep.dto.SleepPageSummaryResponse;
import com.app.sleep.dto.SleepQualityRequest;
import com.app.sleep.entity.SleepLog;
import com.app.sleep.entity.SleepQuality;
import com.app.sleep.service.SleepService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sleep")
@RequiredArgsConstructor
public class SleepController {
    private final SleepService sleepService;

    /** 수면 시작/종료 기록 upsert */
    @PostMapping
    public SleepLogRequest upsertSleep(@RequestBody SleepLogRequest req) {
        SleepLog saved = sleepService.upsertSleepLog(req);
        return SleepLogRequest.builder()
                .userId(saved.getUser().getUserId())
                .sleepAt(saved.getSleepAt())
                .wakeAt(saved.getWakeAt())
                .targetDate(saved.getTargetDate())
                .build();
    }

    /** 수면 품질(0~100) upsert */
    @PostMapping("/quality")
    public SleepQualityRequest upsertQuality(@RequestBody SleepQualityRequest req) {
        SleepQuality saved = sleepService.upsertSleepQuality(req);
        return SleepQualityRequest.builder()
                .userId(saved.getUser().getUserId())
                .targetDate(saved.getTargetDate())
                .score(saved.getScore())
                .build();
    }

    /** 페이지 요약 조회 */
    @GetMapping("/summary")
    public SleepPageSummaryResponse summary(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Double thresholdMg
    ) {
        return sleepService.getSummary(userId, date, thresholdMg);
    }
}
