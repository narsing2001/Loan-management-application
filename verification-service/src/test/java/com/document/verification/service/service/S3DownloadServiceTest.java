package com.document.verification.service.service;
import com.document.verification.service.globalExceptionHandling.customException.S3DownloadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3DownloadServiceTest {

    @InjectMocks
    private S3DownloadService service;

    @Mock
    private S3Client s3Client;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "bucket", "test-bucket");
    }
    @Test
    void download_success() {
        byte[] data = "dummy data".getBytes();
        ResponseInputStream<GetObjectResponse> response =
                new ResponseInputStream<>(
                        GetObjectResponse.builder().build(),
                        new ByteArrayInputStream(data));
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(response);

        File file = service.download("test-key");
        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        verify(s3Client).getObject(any(GetObjectRequest.class));
    }
    @Test
    void download_failure() {
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new S3DownloadException("S3 failed"));
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.download("test-key"));
        assertEquals("Failed to download file from S3", ex.getMessage());
    }
}
