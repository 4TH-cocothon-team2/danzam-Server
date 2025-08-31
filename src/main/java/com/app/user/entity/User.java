package com.app.user.entity;

import com.app.user.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

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

    private Boolean smoke;

    private Boolean pregnancy;

    private Boolean medication;

    private Gender gender;


    public void updateProfile(Gender gender, Integer age, Integer weight, Boolean smoke, Boolean pregnancy, Boolean medication) {
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.smoke = smoke;
        this.pregnancy = pregnancy;
        this.medication = medication;
    }
}
