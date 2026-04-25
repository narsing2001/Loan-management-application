package com.educationloan.document.util;
import com.educationloan.document.globalExceptionHandling.CustomException.S3PresignedUrlException;
import com.educationloan.document.globalExceptionHandling.CustomException.S3UploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3UtilTest {
    @InjectMocks
    private S3Util s3Util;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner presigner;

    @Mock
    private MultipartFile file;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(s3Util, "bucket", "test-bucket");
    }

    @Test
    void uploadFile_success() throws Exception {
        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream("data".getBytes()));
        when(file.getSize()).thenReturn(10L);
        when(file.getContentType()).thenReturn("application/pdf");

        when(s3Client.putObject(
                any(PutObjectRequest.class),
                any(RequestBody.class)
        )).thenReturn(PutObjectResponse.builder().build());

        assertDoesNotThrow(() ->
                s3Util.uploadFile("test-key", file));

        verify(s3Client).putObject(
                any(PutObjectRequest.class),
                any(RequestBody.class)
        );
    }

    @Test
    void uploadFile_failure() throws Exception {
        when(file.getInputStream())
                .thenThrow(new RuntimeException("IO error"));

        assertThrows(S3UploadException.class, () ->
                s3Util.uploadFile("test-key", file));
    }

    @Test
    void generatePreSignedDownloadUrl_success() throws Exception {
        PresignedGetObjectRequest presignedRequest =
                mock(PresignedGetObjectRequest.class);

        when(presignedRequest.url())
                .thenReturn(java.net.URI.create("http://test-url").toURL());

        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        String url = s3Util.generatePreSignedDownloadUrl("key", 5);

        assertNotNull(url);
        assertEquals("http://test-url", url);

        verify(presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void generatePreSignedDownloadUrl_failure() {
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenThrow(new RuntimeException("Presign error"));
        assertThrows(S3PresignedUrlException.class, () ->
                s3Util.generatePreSignedDownloadUrl("key", 5));
    }
}