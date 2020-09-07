package com.chinalife.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cl.message")
public class MessageProperties {
    private int appid;
    private String appkey;
    private int templateId;
    private String smsSign;

}
