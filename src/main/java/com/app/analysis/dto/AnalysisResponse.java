package com.app.analysis.dto;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public record AnalysisResponse (
    double remainingPercent,
    double remainingMg,
    double hoursToZero,
    LocalDateTime estimatedSleepTime,
    List<Point> series
    ) {
    public record Point(LocalDateTime time, double mg) {}
}
