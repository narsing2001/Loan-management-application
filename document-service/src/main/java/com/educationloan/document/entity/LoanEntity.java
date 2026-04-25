package com.educationloan.document.entity;
import com.educationloan.document.enumConst.StudyLocationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(name = "loan")
@Getter
@Setter
public class LoanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal loanAmount;

    @Enumerated(EnumType.STRING)
    private StudyLocationType studyLocationType;
}

