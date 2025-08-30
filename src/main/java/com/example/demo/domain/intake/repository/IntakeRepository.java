package com.example.demo.domain.intake.repository;

import com.example.demo.domain.intake.entity.IntakeRecord;
import com.example.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IntakeRepository extends JpaRepository<IntakeRecord, String> {
    // 특정 사용자의 특정 기간 섭취 기록 조회 (오늘의 기록 조회에 사용)
    List<IntakeRecord> findByUserAndIntakeAtBetween(User user, LocalDateTime start, LocalDateTime end);

    // 특정 사용자의 특정 섭취 기록 조회 (상세조회, 수정, 삭제 시 보안 검증에 사용)
    Optional<IntakeRecord> findByIntakeIdAndUser(String intakeId, User user);
}
