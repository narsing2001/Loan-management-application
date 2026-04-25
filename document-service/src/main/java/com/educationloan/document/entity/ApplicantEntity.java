package com.educationloan.document.entity;
import com.educationloan.document.enumConst.ApplicantType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "applicant")
@Getter
@Setter
public class ApplicantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private LoanEntity loan;

    @Enumerated(EnumType.STRING)
    private ApplicantType applicantType;

    private String name;

    private String email;

    private String mobile;

    private String address;

    private String dob;

    @Column(name = "aadhaar_number")
    private String aadhaarNumber;
}