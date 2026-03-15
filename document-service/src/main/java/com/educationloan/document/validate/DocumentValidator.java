package com.educationloan.document.validate;

import com.educationloan.document.globalExceptionHandling.CustomException.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import org.apache.tika.Tika;
@Component
public class DocumentValidator {

    private static final Tika tika = new Tika();
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; //5MB

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png"
    );
    public  void validateFileType(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Uploaded file must not be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size must be less than or equal to 5 MB");
        }
        try {
            String detectedType = tika.detect(file.getInputStream());
            if (!ALLOWED_MIME_TYPES.contains(detectedType)) {
                throw new InvalidFileException("Allowed types: PDF, JPG, JPEG,PNG");
            }
        } catch (Exception e) {
            throw new RuntimeException("File validation failed", e);
        }
    }
}
