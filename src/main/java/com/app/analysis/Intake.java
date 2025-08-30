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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String userPk; // UUID 문자열

    private double mg;
    private LocalDateTime takenAt;
}
