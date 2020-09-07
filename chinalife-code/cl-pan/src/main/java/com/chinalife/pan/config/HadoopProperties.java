package com.chinalife.pan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cl.hadoop")
public class HadoopProperties {

    private String path;

    private String user;
}
