package com.app.analysis;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="Users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @Column(length = 36)
    //private String userPk; //UUID
    private String userId;

    //private Double weightKg; // 체중
    private Integer weight;

    private Integer age; //나이

    //private Boolean smoker; // 흡연 여부
    private Boolean smoke;

    //private Boolean pregnant; // 임신여부
    private Boolean pregnancy;

    //private Boolean meds; //기타 약물 복용 여부
    private Boolean medication;

}
