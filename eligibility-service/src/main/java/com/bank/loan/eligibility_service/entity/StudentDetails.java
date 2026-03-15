package com.bank.loan.eligibility_service.entity;

import com.bank.loan.eligibility_service.enums.Nationality;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDetails {

    private Integer age;
    private String studentName;

    @Email(message = "Invalid email format")
    private String studentEmail;

    private String studentMobile;
    private String panNumber;
    private String aadhaarNumber;

    @Enumerated(EnumType.STRING)
    private Nationality nationality;

    private String state;
    private  String city;
    private  String pincode;



}
