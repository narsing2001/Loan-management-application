package com.educationloan.document.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantResponseDTO {

    private Long id;
    private String name;
    private String dob;
    private String aadhaarNumber;
}