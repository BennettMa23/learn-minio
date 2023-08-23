package com.tuling.miniodemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author Fox
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;

}
