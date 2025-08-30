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
    private String userId; // Flutter에서 생성한 UUID 저장

    private Integer age;

    private Integer weight;

    private boolean smoke;

    private boolean pregnancy;

    private boolean medication;

    private Gender gender;


    public void updateProfile(Gender gender, Integer age, Integer weight, boolean smoke, boolean pregnancy, boolean medication) {
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.smoke = smoke;
        this.pregnancy = pregnancy;
        this.medication = medication;
    }
}
