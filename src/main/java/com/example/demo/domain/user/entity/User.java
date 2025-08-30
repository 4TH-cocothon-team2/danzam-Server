package com.example.demo.domain.user.entity;

import com.example.demo.domain.user.enums.Gender;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.sql.Time;

@Entity
@Table(name = "Users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    private String user_id; // Flutter에서 생성한 UUID 저장

    private Integer age;

    private Integer weight;

    private boolean smoke;

    private boolean pregnancy;

    private boolean medication;

    private Gender gender;

    private Time sleep;
}
