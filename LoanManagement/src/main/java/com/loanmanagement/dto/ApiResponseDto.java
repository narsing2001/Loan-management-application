package com.loanmanagement.dto;
import com.loanmanagement.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDto {
    private String message;
    private Status status;
    private LocalDate date;

}
