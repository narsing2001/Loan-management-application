package com.example.LoanTypeStrategyInterest.dto.dto1;

import com.example.LoanTypeStrategyInterest.enums.enum1.ApplicationStatus;
import com.example.LoanTypeStrategyInterest.enums.enum1.CourseType;
import com.example.LoanTypeStrategyInterest.enums.enum1.LoanType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
public class UserApplicationStatusUpdateResponseDTO {
    private UUID id;
    private String applicantName;
    private ApplicationStatus applicationStatus;
    private LoanType loanType;
    private CourseType courseType;
    private LocalDateTime updatedAt;
}
