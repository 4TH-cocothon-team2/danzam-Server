package com.example.demo.domain.intake.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;

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
    private String intake_id;

    private String name;

    private Integer count;

    private Integer capacity;

    private Integer caffeine_mg;

    private Time intake_time;

    private Date intake_date;

}
