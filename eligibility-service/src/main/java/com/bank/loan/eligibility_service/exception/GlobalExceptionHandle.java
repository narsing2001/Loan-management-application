package com.bank.loan.eligibility_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusiness(BusinessException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvalidAgeException.class)
     public ResponseEntity<String> handleInvalidAgeException(InvalidAgeException InvalidAge){
        return new ResponseEntity<>(InvalidAge.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ve){
        return  new ResponseEntity<>(ve.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPhoneNumber.class)
    public ResponseEntity<String> handleInvalidPhoneException(InvalidPhoneNumber phone){
        return new ResponseEntity<>(phone.getMessage(),HttpStatus.BAD_REQUEST);
    }


}
