package com.app.analysis;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserProfile {
    @Id
    @Column(length = 36)
    private String userPk; //UUID

    private Double weightKg; // 체중
    private Integer age; //나이
    private Boolean smoker; // 흡연 여부
    private Boolean pregnant; // 임신여부
    private Boolean meds; //기타 약물 복용 여부


}
