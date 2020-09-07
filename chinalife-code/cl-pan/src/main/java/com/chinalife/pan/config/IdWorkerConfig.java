package com.chinalife.pan.config;

import com.chinalife.common.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IdWorkerPropertise.class)
public class IdWorkerConfig {

    @Bean
    public IdWorker idWorker(IdWorkerPropertise prop) {
        return new IdWorker(prop.getWorkerId(),prop.getDataCenterId());
    }

}
