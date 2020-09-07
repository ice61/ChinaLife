package com.chinalife.mq;

import com.chinalife.utils.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class MessageListener {

    @Autowired
    private MessageUtil messageUtil;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "message.verify.code.queue",durable = "true"),
            exchange = @Exchange(name = "cl.sms.exchange",type = ExchangeTypes.TOPIC),
            key = {"sms.verify.code"}
    ))
    public void listen(Map<String,String> msg) {
        if(CollectionUtils.isEmpty(msg)) {
            return;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isBlank(phone)) {
            return;
        }
        messageUtil.sendMessage(phone,msg.get("code"));
    }

    /*@RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "message.verify.code.queue",durable = "true"),
            exchange = @Exchange(name = "cl.sms.exchange",type = ExchangeTypes.TOPIC),
            key = {"sms.verify.code"}
    ))
    public void listen(Map<String,String> msg) {
        if(CollectionUtils.isEmpty(msg)) {
            return;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isBlank(phone)) {
            return;
        }
        //messageUtil.sendMessage(phone,msg.get("code"));
        System.out.println(msg.get("code"));
    }*/

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "message.password.code.queue",durable = "true"),
            exchange = @Exchange(name = "cl.sms.exchange",type = ExchangeTypes.TOPIC),
            key = {"sms.password.code"}
    ))
    public void listenChangePassword(Map<String,String> msg) {
        if(CollectionUtils.isEmpty(msg)) {
            return;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isBlank(phone)) {
            return;
        }
        messageUtil.updatePsMessage(phone,msg.get("code"));
    }
}
