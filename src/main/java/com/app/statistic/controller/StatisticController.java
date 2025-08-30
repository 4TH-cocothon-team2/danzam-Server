package com.app.statistic.controller;

import com.app.statistic.dto.StatisticResponse;
import com.app.statistic.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping
    public ResponseEntity<StatisticResponse> getMyStatistics(
            @RequestAttribute("guestUuid") String uuid) {
        StatisticResponse response = statisticService.getMyStatistics(uuid);
        return ResponseEntity.ok(response);
    }

}
