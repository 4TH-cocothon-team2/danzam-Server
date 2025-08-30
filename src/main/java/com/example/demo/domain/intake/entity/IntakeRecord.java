package com.example.demo.domain.intake.entity;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@Table(name = "IntakeRecords")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntakeRecord {

    @Id
    @Column(name = "intake_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String intakeId;

    @ManyToOne(fetch = FetchType.LAZY) // 2. User와의 관계 설정 (다대일)
    @JoinColumn(name = "user_id", nullable = false) // DB에는 user_id 컬럼으로 저장됨
    private User userId;

    private String name;

    private Integer count;

    private Integer capacity;

    private Integer caffeine_mg;


    private LocalDateTime intakeAt;

    public void update(String name, Integer count, Integer capacity, Integer caffeine_mg,LocalDateTime intakeAt) {
        this.name = name;
        this.count = count;
        this.capacity = capacity;
        this.caffeine_mg = caffeine_mg;
        this.intakeAt = intakeAt;
    }
}
