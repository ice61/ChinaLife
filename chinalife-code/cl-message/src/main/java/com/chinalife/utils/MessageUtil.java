package com.chinalife.utils;

import com.chinalife.config.MessageProperties;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@EnableConfigurationProperties(MessageProperties.class)
@Slf4j
public class MessageUtil {

    @Autowired
    private MessageProperties prop;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "message:phone";
    private static final long MESSAGE_MIN_INTERVAL = 60000;
    private static final String PASSWORD_PREFIX = "message:updatePassword";

    public void sendMessage(String phone, String code) {
        String lastTime = redisTemplate.opsForValue().get(KEY_PREFIX + phone);
        if (StringUtils.isNoneBlank(lastTime)) {
            Long last = Long.valueOf(lastTime);
            if (System.currentTimeMillis() - last < MESSAGE_MIN_INTERVAL) {
                log.info("[短信服务] 发送短信频率过高，被拦截");
                return;
            }
        }
        try {
            String[] phoneNumbers = {phone};
            String[] params = {code, "5"};
            SmsSingleSender ssender = new SmsSingleSender(prop.getAppid(), prop.getAppkey());
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
                    prop.getTemplateId(), params, prop.getSmsSign(), "", "");
            redisTemplate.opsForValue().set(KEY_PREFIX + phone, String.valueOf(System.currentTimeMillis()),
                    1, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("[短信服务] 发送短信异常", e);
            return;
        }

    }

    public void updatePsMessage(String phone, String code) {
        String lastTime = redisTemplate.opsForValue().get(PASSWORD_PREFIX + phone);
        if (StringUtils.isNoneBlank(lastTime)) {
            Long last = Long.valueOf(lastTime);
            if (System.currentTimeMillis() - last < MESSAGE_MIN_INTERVAL) {
                log.info("[短信服务] 发送短信频率过高，被拦截");
                return;
            }
        }
        try {
            String[] phoneNumbers = {phone};
            String[] params = {code, "5"};
            SmsSingleSender ssender = new SmsSingleSender(prop.getAppid(), prop.getAppkey());
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
                    prop.getTemplateId(), params, prop.getSmsSign(), "", "");
            redisTemplate.opsForValue().set(PASSWORD_PREFIX + phone, String.valueOf(System.currentTimeMillis()),
                    1, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("[短信服务] 发送短信异常", e);
            return;
        }
    }

}
