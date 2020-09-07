package com.chinalife.upload.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cl.upload")
@Data
public class UploadProperties {

     private String baseUrl;

     private List<String> allowTypes;

}
