package com.app.analysis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IntakeRepository extends JpaRepository<Intake, Long> {
    List<Intake> findByUserAndIntakeAtBetween(User user, LocalDateTime start, LocalDateTime end);
}
