package com.document.verification.service.parser;

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
                .orElseThrow(() ->
                        new RuntimeException("Parser not found for " + type));
    }
}