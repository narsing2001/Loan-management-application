package com.educationloan.document.entity;

import com.educationloan.document.enumConst.ApplicantType;
import com.educationloan.document.enumConst.StudyLocationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "document_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRuleEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_location_type")
    private StudyLocationType studyLocationType;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "document_rule_applicants",
            joinColumns = @JoinColumn(name = "rule_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "applicant_type")
    private Set<ApplicantType> applicantTypes;

    private Instant createdAt;
}