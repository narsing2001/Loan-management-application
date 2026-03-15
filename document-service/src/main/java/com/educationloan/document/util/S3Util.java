package com.educationloan.document.util;

import com.educationloan.document.globalExceptionHandling.CustomException.S3PresignedUrlException;
import com.educationloan.document.globalExceptionHandling.CustomException.S3UploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.io.InputStream;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3Util {
    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public void uploadFile(String key, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(is, file.getSize())
            );

        } catch (Exception e) {
            throw new S3UploadException(
                    "Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    public String generatePreSignedDownloadUrl(String key, int minutes) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest =
                    GetObjectPresignRequest.builder()
                            .getObjectRequest(getRequest)
                            .signatureDuration(Duration.ofMinutes(minutes))
                            .build();

            return presigner
                    .presignGetObject(presignRequest)
                    .url()
                    .toString();

        } catch (Exception e) {
            throw new S3PresignedUrlException(
                    "Failed to generate presigned URL: " + e.getMessage());
        }
    }
}