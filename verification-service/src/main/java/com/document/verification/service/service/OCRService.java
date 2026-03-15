package com.document.verification.service.service;

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
                throw new RuntimeException("Invalid image file");
            }
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("eng");

            return tesseract.doOCR(image);

        } catch (Exception e) {
            throw new RuntimeException("OCR failed", e);
        }
    }
}