package com.shenhua.filectl.common.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "file-system")
public class SupportUploadFileTypeConfig {
    private List<String> supportUploadFileType;
}
