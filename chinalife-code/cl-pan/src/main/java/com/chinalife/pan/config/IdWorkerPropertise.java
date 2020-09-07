package com.chinalife.pan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cl.worker")
public class IdWorkerPropertise {

    private Long workerId;

    private Long dataCenterId;

}
