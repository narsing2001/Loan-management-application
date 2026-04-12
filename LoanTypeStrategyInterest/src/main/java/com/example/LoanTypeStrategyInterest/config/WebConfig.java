package com.example.LoanTypeStrategyInterest.config;


import com.example.LoanTypeStrategyInterest.converter.StringToApplicationStatusConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final StringToApplicationStatusConverter stringToApplicationStatusConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToApplicationStatusConverter);
    }
}