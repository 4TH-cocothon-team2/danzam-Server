package com.app.sleep.entity;


import com.app.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sleep_logs",
        indexes =  {@Index(name="idx_sleep_user_date", columnList = "user_id,targetDate")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SleepLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 사용자가 기록한 수면 시작/종료
    private LocalDateTime sleepAt;
    private LocalDateTime wakeAt;

    // 이 기록이 속하는 기준 날짜(페이지용 필터키: 보통 수면 시작일 기준)
    @Column(nullable = false)
    private LocalDate targetDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (targetDate == null && sleepAt != null) targetDate = sleepAt.toLocalDate();
    }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }
}

