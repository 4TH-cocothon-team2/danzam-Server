package com.app.sleep.entity;

import com.app.analysis.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sleep_qualities",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","targetDate"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SleepQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 0~100
    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private LocalDate targetDate;      // 페이지 기준 날짜(수면 기록과 동일 키)

    private LocalDateTime ratedAt;

    @PrePersist
    void onCreate() { ratedAt = LocalDateTime.now(); }
}
