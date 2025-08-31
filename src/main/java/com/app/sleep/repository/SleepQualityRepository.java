package com.app.sleep.repository;


import com.app.sleep.entity.SleepQuality;
import com.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SleepQualityRepository extends JpaRepository<SleepQuality, Long> {
    Optional<SleepQuality> findByUserAndTargetDate(User user, LocalDate targetDate);
}
