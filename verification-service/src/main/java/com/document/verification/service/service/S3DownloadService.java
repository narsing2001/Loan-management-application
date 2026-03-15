package com.document.verification.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class S3DownloadService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public File download(String key) {

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> response =
                    s3Client.getObject(request);

            File tempFile = File.createTempFile("document-", ".tmp");

            Files.copy(response, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return tempFile;

        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }
}
