package com.document.verification.service.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParsedDocumentDTO {
    private String name;
    private String dob;
    private String documentNumber;
}
