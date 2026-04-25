package com.document.verification.service.parser;
import com.document.verification.service.globalExceptionHandling.customException.ParserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParserFactoryTest {
    @Mock
    private DocumentParser parser;
    @InjectMocks
    private ParserFactory parserFactory;

    @Test
    void getParser_shouldReturnParser() {
        when(parser.supports("AADHAAR")).thenReturn(true);
        parserFactory = new ParserFactory(List.of(parser));
        DocumentParser result = parserFactory.getParser("AADHAAR");
        assertNotNull(result);
    }

    @Test
    void getParser_notFound_shouldThrowException() {
        when(parser.supports("UNKNOWN")).thenReturn(false);
        parserFactory = new ParserFactory(List.of(parser));
        assertThrows(ParserNotFoundException.class,
                () -> parserFactory.getParser("UNKNOWN"));
    }
}
