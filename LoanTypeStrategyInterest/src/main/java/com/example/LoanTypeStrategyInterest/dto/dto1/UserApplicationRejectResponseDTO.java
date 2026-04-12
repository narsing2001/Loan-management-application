package com.example.LoanTypeStrategyInterest.dto.dto1;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserApplicationRejectResponseDTO {
    @NotNull
    private UUID applicationId;
    @NotNull
    private String status;
    private String reasonCode;
    private String message;
    private String rejectedBy;
    private String approvedBy;
    private List<String> eligibilityCriteriaFailed;
}