package com.bank.loan.eligibility_service.entity;

import com.bank.loan.eligibility_service.enums.CollegeCategory;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationDetails {

    private String courseName;
    private Integer courseDurationMonths;
    private String collegeName;
    private String universityName;
    private Boolean admissionConfirmed;
    private Integer expectedGraduationYear;
    private Double academicPercentage;
    private String admissionReferenceNumber;

    @Enumerated(EnumType.STRING)
    private CollegeCategory collegeCategory;
}
