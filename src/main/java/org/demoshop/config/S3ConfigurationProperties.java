package org.demoshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "s3")
public class S3ConfigurationProperties {

    private String accessKey;
    private String secretKey;
    private String endPoint;
    private String region;
}
