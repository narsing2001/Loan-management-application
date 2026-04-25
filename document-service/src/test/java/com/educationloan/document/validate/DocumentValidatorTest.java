package com.educationloan.document.validate;

import com.educationloan.document.globalExceptionHandling.CustomException.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentValidatorTest {

    @InjectMocks
    private DocumentValidator validator;

    @Mock
    private MultipartFile file;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(validator, "maxFileSize", "1MB");
        validator.init();
    }

    @Test
    void shouldPassForValidPdf() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(500L);

        byte[] pdfBytes = "%PDF-1.4".getBytes();

        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream(pdfBytes));
        when(file.getOriginalFilename())
                .thenReturn("test.pdf");

        validator.validateFileType(file);
    }

    @Test
    void shouldThrowExceptionWhenFileIsNull() {
        assertThrows(InvalidFileException.class,
                () -> validator.validateFileType(null));
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);

        assertThrows(InvalidFileException.class,
                () -> validator.validateFileType(file));
    }

    @Test
    void shouldThrowExceptionWhenFileSizeExceedsLimit() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(5_000_000L); // large

        assertThrows(InvalidFileException.class,
                () -> validator.validateFileType(file));
    }

    @Test
    void shouldThrowExceptionForInvalidFileType() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(500L);
        when(file.getInputStream()).thenReturn(
                new ByteArrayInputStream("dummy".getBytes()));
        when(file.getOriginalFilename()).thenReturn("test.exe");

        assertThrows(InvalidFileException.class,
                () -> validator.validateFileType(file));
    }

    @Test
    void shouldHandleExceptionFromInputStream() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(500L);
        when(file.getInputStream()).thenThrow(new RuntimeException());

        assertThrows(InvalidFileException.class,
                () -> validator.validateFileType(file));
    }
}
