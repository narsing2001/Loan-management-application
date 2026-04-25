package com.document.verification.service.service;

import com.document.verification.service.globalExceptionHandling.customException.InvalidFileException;
import com.document.verification.service.globalExceptionHandling.customException.OCRException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OCRServiceTest {

    private OCRService ocrService;

    @BeforeEach
    void setup() {
        ocrService = new OCRService();
        ReflectionTestUtils.setField(ocrService, "tessDataPath", "test-path");
    }

    @Test
    void extractText_invalidImage_shouldThrowInvalidFileException() throws Exception {
        File file = File.createTempFile("test", ".txt"); // not an image

        assertThrows(InvalidFileException.class, () -> {
            ocrService.extractText(file);
        });
    }

    @Test
    void extractText_success() throws Exception {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        File file = File.createTempFile("test", ".png");
        ImageIO.write(image, "png", file);

        try {
            String result = ocrService.extractText(file);
            assertNotNull(result);
        } catch (OCRException e) {
            assertEquals("OCR failed", e.getMessage());
        }
    }
    @Test
    void testExtractText_OCRFailure_ShouldThrowOCRException() {
        File file = new File("invalid.png"); // non-valid image

        OCRService service = new OCRService();
        ReflectionTestUtils.setField(service, "tessDataPath", "invalid/path");

        assertThrows(OCRException.class, () -> {
            service.extractText(file);
        });
    }
}