package com.example.demo.domain.sleep.repository;

import com.example.demo.domain.sleep.entity.Sleep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepRepository extends JpaRepository<Sleep, Long> {
}
