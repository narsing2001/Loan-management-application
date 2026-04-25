package com.document.verification.service.service;
import com.document.verification.service.globalExceptionHandling.customException.InvalidFileException;
import com.document.verification.service.globalExceptionHandling.customException.OCRException;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class OCRService {
    @Value("${ocr.tessdata-path}")
    private String tessDataPath;

    public String extractText(File file) {
        try {
            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                throw new InvalidFileException("Invalid image file");
            }
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("eng");

            return tesseract.doOCR(image);

        } catch (InvalidFileException e) {
            throw e;
        } catch (Exception e) {
            throw new OCRException("OCR failed", e);
        }
    }
}