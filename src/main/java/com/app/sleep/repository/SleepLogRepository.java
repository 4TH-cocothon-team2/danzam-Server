package com.app.sleep.repository;

import com.app.sleep.entity.SleepLog;
import com.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {
    Optional<SleepLog> findByUserAndTargetDate(User user, LocalDate targetDate);
}
