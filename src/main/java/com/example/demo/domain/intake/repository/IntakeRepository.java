package com.example.demo.domain.intake.repository;

import com.example.demo.domain.intake.entity.IntakeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeRepository extends JpaRepository<IntakeRecord, String> {
}
