package com.app.analysis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "intakes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Intake {

    @Id
    @Column(name = "intake_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //private Long id;
    private Long intakeId;

    //@Column(nullable = false, length = 36)
    //private String userPk; // UUID 문자열
    @ManyToOne(fetch = FetchType.LAZY) // 2. User와의 관계 설정 (다대일)
    @JoinColumn(name = "user_id", nullable = false) // DB에는 user_id 컬럼으로 저장됨
    private User user;

    //private double mg;
    private Integer caffeine_mg;

    //private LocalDateTime takenAt;
    private LocalDateTime intakeAt;
}
