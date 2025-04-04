package org.envelope.imageservice.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3ClientConfig {
    @Value("${minio.user}")
    private String username;
    @Value("${minio.password}")
    private String secretKey;
    @Value("${minio.url}")
    private String minioUrl;
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(username, secretKey)
                .build();
    }
}
