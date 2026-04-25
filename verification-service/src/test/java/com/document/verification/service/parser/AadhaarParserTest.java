package com.document.verification.service.parser;
import com.document.verification.service.dto.ParsedDocumentDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AadhaarParserTest {
    private final AadhaarParser parser = new AadhaarParser();
    @Test
    void supports_shouldReturnTrueForAadhaar() {
        assertTrue(parser.supports("AADHAAR"));
    }

    @Test
    void supports_shouldReturnFalseForOtherType() {
        assertFalse(parser.supports("PAN"));
    }
    @Test
    void parse_shouldExtractAllFields() {
        String text = """
                Government of India
                John Doe
                DOB: 12/05/1995
                Aadhaar No: 2345 6789 1234
                """;

        ParsedDocumentDTO result = parser.parse(text);
        assertEquals("John Doe", result.getName());
        assertEquals("12/05/1995", result.getDob());
        assertEquals("234567891234", result.getDocumentNumber());
    }
    @Test
    void parse_shouldExtractAadhaarWithDashes() {
        String text = """
                Name
                Jane Doe
                DOB: 01/01/2000
                Aadhaar: 2345-6789-1234
                """;
        ParsedDocumentDTO result = parser.parse(text);
        assertEquals("Jane Doe", result.getName());
        assertEquals("01/01/2000", result.getDob());
        assertEquals("2345-6789-1234".replaceAll("\\s", ""), result.getDocumentNumber());
    }
    @Test
    void parse_missingAadhaar_shouldReturnNull() {
        String text = """
                John Doe
                DOB: 10/10/1990
                """;
        ParsedDocumentDTO result = parser.parse(text);
        assertEquals("John Doe", result.getName());
        assertEquals("10/10/1990", result.getDob());
        assertNull(result.getDocumentNumber());
    }
    @Test
    void parse_missingDob_shouldReturnNull() {
        String text = """
                John Doe
                Aadhaar: 234567891234
                """;
        ParsedDocumentDTO result = parser.parse(text);
        assertNull(result.getDob());
        assertEquals("234567891234", result.getDocumentNumber());
    }

    @Test
    void parse_missingName_shouldReturnNull() {
        String text = """
                DOB: 12/12/2000
                Aadhaar: 234567891234
                """;

        ParsedDocumentDTO result = parser.parse(text);
        assertNull(result.getName());
    }
    @Test
    void extractName_shouldIgnoreShortOrInvalidName() {
        String text = """
                S A
                DOB: 12/12/2000
                """;
        String name = parser.extractName(text);
        assertNull(name);
    }
}