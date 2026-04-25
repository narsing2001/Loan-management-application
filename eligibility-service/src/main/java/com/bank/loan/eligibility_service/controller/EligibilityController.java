package com.bank.loan.eligibility_service.controller;

import com.bank.loan.eligibility_service.dto.EligibilityRequestDTO;
import com.bank.loan.eligibility_service.dto.EligibilityResponseDTO;
import com.bank.loan.eligibility_service.entity.LoanEligibility;
import com.bank.loan.eligibility_service.service.EligibilityService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eligibility")
@Slf4j
public class EligibilityController {

    private final EligibilityService eligibilityService;

    public EligibilityController(EligibilityService eligibilityService){
        this.eligibilityService = eligibilityService;
    }

    @PostMapping("/check")
    public ResponseEntity<EligibilityResponseDTO> checkEligibility(
            @Valid @RequestBody EligibilityRequestDTO request) {

        log.info("Received eligibility check request: {}", request);

        EligibilityResponseDTO response = eligibilityService.checkEligibility(request);

        log.info("Eligibility check completed. Result: {}", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public  ResponseEntity<List<LoanEligibility>> getAll(){

        log.info("Fetching all eligibility records");

        return ResponseEntity.ok(eligibilityService.getAllEligibility());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanEligibility> getById(@PathVariable Long id){

        log.info("Fetching eligibility for ID: {}", id);

        return ResponseEntity.ok(eligibilityService.getEligibilityById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){

        log.info("Deleting eligibility record for ID: {}", id);

        eligibilityService.deleteEligibility(id);

        log.info("Record deleted successfully for ID: {}", id);

        return ResponseEntity.ok("record deleted successfully");
    }


}
