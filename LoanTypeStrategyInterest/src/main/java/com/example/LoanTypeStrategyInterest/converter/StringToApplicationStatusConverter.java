package com.example.LoanTypeStrategyInterest.converter;



import com.example.LoanTypeStrategyInterest.enums.enum1.ApplicationStatus;
import com.example.LoanTypeStrategyInterest.exception.InvalidStatusException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToApplicationStatusConverter implements Converter<String, ApplicationStatus> {

    @Override
    public ApplicationStatus convert(String source) {
        try {
            return ApplicationStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Invalid status value: " + source, source);
        }
    }
}