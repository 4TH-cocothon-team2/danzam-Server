package com.app.statistic.controller;

import com.app.statistic.dto.StatisticResponse;
import com.app.statistic.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping
    public ResponseEntity<StatisticResponse> getMyStatistics(
            @RequestHeader("X-User-Id") String userId) {
        StatisticResponse response = statisticService.getMyStatistics(userId);
        return ResponseEntity.ok(response);
    }

}
