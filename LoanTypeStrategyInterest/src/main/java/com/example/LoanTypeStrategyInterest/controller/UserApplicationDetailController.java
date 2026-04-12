package com.example.LoanTypeStrategyInterest.controller;


import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationResponseDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationStatusResponseDTO;
import com.example.LoanTypeStrategyInterest.enums.enum1.ApplicationStatus;
import com.example.LoanTypeStrategyInterest.enums.enum1.LoanType;
import com.example.LoanTypeStrategyInterest.response.ApiResponse;
import com.example.LoanTypeStrategyInterest.service.serv1.UserApplicationDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/loans")
public class UserApplicationDetailController {
    private final UserApplicationDetailService userApplicationDetailService;

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<UserApplicationResponseDTO>> submitUserApplication(@Valid @RequestBody UserApplicationRequestDTO userApplicationRequestDTO){
        UserApplicationResponseDTO applicant=userApplicationDetailService.applyLoan(userApplicationRequestDTO);
        ApiResponse<UserApplicationResponseDTO> response=ApiResponse.success("Loan application submitted successfully...!",applicant);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/get/{status}")
    public ResponseEntity<ApiResponse<List<UserApplicationStatusResponseDTO>>>getApplicantByStatus(@PathVariable("status")ApplicationStatus status){

        List<UserApplicationStatusResponseDTO> applicant=userApplicationDetailService.getByStatus(status);
        ApiResponse<List<UserApplicationStatusResponseDTO>> response = ApiResponse.success("Fetched Loan application by status successfully..!", applicant);
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/get/{loanType}")
    public ResponseEntity<ApiResponse<List<UserApplicationStatusResponseDTO>>> getApplicationByLoanType(@PathVariable("loanType")LoanType loanType){
        List<UserApplicationStatusResponseDTO> applicant =userApplicationDetailService.getByLoanType(loanType);
        ApiResponse<List<UserApplicationStatusResponseDTO>> response=ApiResponse.success("Fetched Loan application by loanType successfully...!",applicant);
        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }





}
