package com.apimgt.apiordermgt.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.database")
public class DatabaseProperties {

    private String filePath;

}
