package com.example.LoanTypeStrategyInterest.dto.dto1;

import com.example.LoanTypeStrategyInterest.enums.enum1.ApplicationStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateStatusRequest {
    private UUID id;
    private ApplicationStatus newStatus;
}