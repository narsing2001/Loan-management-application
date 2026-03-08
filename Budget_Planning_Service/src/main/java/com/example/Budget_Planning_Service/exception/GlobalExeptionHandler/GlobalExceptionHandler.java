package com.example.Budget_Planning_Service.exception.GlobalExeptionHandler;

import com.example.Budget_Planning_Service.exception.BudgetExceededException;
import com.example.Budget_Planning_Service.exception.InvalidStateTransitionException;
import com.example.Budget_Planning_Service.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> onNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BudgetExceededException.class)
    public ResponseEntity<String> onBudgetExceeded(BudgetExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<String> onInvalidState(InvalidStateTransitionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> onValidation(MethodArgumentNotValidException ex) {
        String err = ex.getBindingResult().getFieldErrors()
                .stream().map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error(s): " + err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> onGeneric(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
}