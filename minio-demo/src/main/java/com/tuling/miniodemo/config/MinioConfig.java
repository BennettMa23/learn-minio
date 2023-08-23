package com.tuling.miniodemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

/**
 * @author Fox
 */
@Configuration
public class MinioConfig {

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient(){
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minioProperties.getEndpoint())
                        .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                        .build();
        return minioClient;
    }
}
