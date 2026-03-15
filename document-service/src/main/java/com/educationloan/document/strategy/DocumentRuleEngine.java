package com.educationloan.document.strategy;

import com.educationloan.document.enumConst.ApplicantType;
import com.educationloan.document.enumConst.StudyLocationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentRuleEngine {

    private final Map<String, DocumentRuleStrategy> strategies;

    public Set<ApplicantType> getAllowedApplicants(
            StudyLocationType studyLocation,
            BigDecimal loanAmount) {

        return strategies
                .get(studyLocation.name())
                .getAllowedApplicants(loanAmount);
    }
}