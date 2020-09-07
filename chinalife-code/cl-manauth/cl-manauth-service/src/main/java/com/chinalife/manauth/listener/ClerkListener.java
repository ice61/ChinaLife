package com.chinalife.manauth.listener;

import com.chinalife.manauth.service.ManauthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClerkListener {

    @Autowired
    private ManauthService manauthService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "manauth.clerk.insert.queue", durable = "true"),
            exchange = @Exchange(name = "cl.clerk.exchange",type = ExchangeTypes.TOPIC),
            key = {"clerk.update"}
    ))
    public void listenInsertOrUpdate(String clerkId) {
        if (StringUtils.isBlank(clerkId)) {
            return;
        }
        manauthService.updateToken(clerkId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "manauth.clerk.password.update.queue", durable = "true"),
            exchange = @Exchange(name = "cl.clerk.exchange",type = ExchangeTypes.TOPIC),
            key = {"clerk.password.update"}
    ))
    public void listenPasswordUpdate(String clerkId) {
        if (StringUtils.isBlank(clerkId)) {
            return;
        }
        manauthService.delToken(clerkId);
    }

}
