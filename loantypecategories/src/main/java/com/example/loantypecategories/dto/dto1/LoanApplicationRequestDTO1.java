package com.example.loantypecategories.dto.dto1;

import com.example.loantypecategories.constant.cont1.BankType;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanApplicationRequestDTO1 {

    @NotBlank(message = "Applicant name is required")
    private String applicantName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String applicantEmail;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Bank selection is required")
    private BankType bankType;  // NEW: Bank selection

    @NotNull(message = "Loan type is required — choose DOMESTIC or INTERNATIONAL")
    private LoanType loanType;

    @NotNull(message = "Course type is required")
    private CourseType courseType;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "10000", message = "Minimum loan amount is ₹10,000")
    private BigDecimal requestedAmount;

    // Expense breakdown (optional - if not provided, defaults will be used)
    private BigDecimal tuitionFee;
    private BigDecimal accommodationCost;
    private BigDecimal travelExpense;
    private BigDecimal laptopExpense;
    private BigDecimal examFee;
    private BigDecimal otherExpense;

    // Verification documents
    private boolean hasAdmissionLetter;
    private boolean institutionApproved;

    // Collateral information
    private boolean hasCollateral;
    private BigDecimal collateralValue;

    // Preferred tenure (optional)
    private Integer preferredTenureYears;
}