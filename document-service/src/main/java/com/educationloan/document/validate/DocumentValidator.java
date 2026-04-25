package com.educationloan.document.validate;
import com.educationloan.document.globalExceptionHandling.CustomException.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Set;

@Slf4j
@Component
public class DocumentValidator {
    private static final Tika tika = new Tika();

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    private long maxFileSizeInBytes;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png");

    @PostConstruct
    public void init() {
        this.maxFileSizeInBytes = DataSize.parse(maxFileSize).toBytes();
    }

    public void validateFileType(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Uploaded file must not be empty");
        }

        if (file.getSize() > maxFileSizeInBytes) {
            throw new InvalidFileException("File size must be less than or equal to " + maxFileSize);
        }

        try (InputStream inputStream = file.getInputStream()) {

            String detectedType = tika.detect(inputStream, file.getOriginalFilename());

            if (detectedType != null && detectedType.contains(";")) {
                detectedType = detectedType.split(";")[0];
            }

            if (!ALLOWED_MIME_TYPES.contains(detectedType)) {
                throw new InvalidFileException("Allowed types: PDF, JPG, JPEG, PNG");
            }

        } catch (InvalidFileException e) {
            throw e;
        } catch (Exception e) {
            log.error("File validation failed", e);
            throw new InvalidFileException("File validation failed");
        }
    }
}
