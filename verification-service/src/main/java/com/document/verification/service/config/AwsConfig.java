package com.document.verification.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {
        @Bean
        public S3Client s3Client(
                @Value("${AWS_ACCESS_KEY_ID}") String accessKey,
                @Value("${AWS_SECRET_ACCESS_KEY}") String secretKey,
                @Value("${AWS_REGION}") String region
        ) {
            AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .build();
        }
}
