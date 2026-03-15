package com.document.verification.service.parser;

import com.document.verification.service.dto.ParsedDocumentDTO;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AadhaarParser implements DocumentParser {

    private static final Pattern AADHAAR =
            Pattern.compile("\\b[2-9]\\d{3}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b");

    private static final Pattern DOB =
            Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");

    @Override
    public boolean supports(String documentType) {
        return documentType.equalsIgnoreCase("AADHAAR");
    }

    @Override
    public ParsedDocumentDTO parse(String text) {

        String number = extractAadhaar(text);
        String dob = extractDob(text);
        String name = extractName(text);

        return ParsedDocumentDTO.builder()
                .name(name)
                .dob(dob)
                .documentNumber(number)
                .build();
    }

    private String extractAadhaar(String text) {

        Matcher matcher = AADHAAR.matcher(text);

        if (matcher.find()) {
            return matcher.group().replaceAll("\\s", "");
        }
        return null;
    }
    private String extractDob(String text) {

        Matcher matcher = DOB.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

public String extractName(String text) {

    String[] lines = text.split("\\n");

    for (int i = 0; i < lines.length; i++) {

        String line = lines[i].toLowerCase();

        if (line.contains("dob")) {

            if (i > 0) {

                String nameLine = lines[i - 1];

                nameLine = nameLine.replaceAll("[^A-Za-z ]", "").trim();

                nameLine = nameLine.replaceAll("^(SA|S|A)\\s+", "");

                if (nameLine.length() > 3) {
                    return nameLine;
                }
            }
        }
    }
    return null;
}
}
