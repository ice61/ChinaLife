package com.chinalife.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cl.filter")
@Data
public class FilterProperties {

    private List<String> allowPaths;
}
