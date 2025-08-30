package com.app.sleep.repository;

import com.app.analysis.User;
import com.app.sleep.entity.SleepQuality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SleepQualityRepository extends JpaRepository<SleepQuality, Long> {
    Optional<SleepQuality> findByUserAndTargetDate(User user, LocalDate targetDate);
}
