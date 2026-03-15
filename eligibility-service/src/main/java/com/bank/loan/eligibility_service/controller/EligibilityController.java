package com.bank.loan.eligibility_service.controller;

import com.bank.loan.eligibility_service.dto.EligibilityRequestDTO;
import com.bank.loan.eligibility_service.dto.EligibilityResponseDTO;
import com.bank.loan.eligibility_service.entity.LoanEligibility;
import com.bank.loan.eligibility_service.service.EligibilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eligibility")
@RequiredArgsConstructor
public class EligibilityController {


    private final EligibilityService eligibilityService;

    @PostMapping("/check")
    public ResponseEntity<EligibilityResponseDTO> checkEligibility(
            @Valid @RequestBody EligibilityRequestDTO request) {

        EligibilityResponseDTO response = eligibilityService.checkEligibility(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public  ResponseEntity<List<LoanEligibility>> getAll(){
        return ResponseEntity.ok(eligibilityService.getAllEligibility());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanEligibility> getById(@PathVariable Long id){
        return ResponseEntity.ok(eligibilityService.getEligibilityById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        eligibilityService.deleteEligibility(id);
        return ResponseEntity.ok("record deleted successfully");
    }


}
