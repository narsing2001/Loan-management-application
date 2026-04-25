package com.document.verification.service.parser;

import com.document.verification.service.globalExceptionHandling.customException.ParserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ParserFactory {
    private final List<DocumentParser> parsers;

    public DocumentParser getParser(String type) {
        return parsers.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow(() -> new ParserNotFoundException("Parser not found for " + type));
    }
}