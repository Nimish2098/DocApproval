package com.Project.DocApproval.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.file")
@Component
public class FileStorageProperties {

    private String uploadDir;
}
