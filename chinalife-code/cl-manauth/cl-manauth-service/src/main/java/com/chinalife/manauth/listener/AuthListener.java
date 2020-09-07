package com.chinalife.manauth.listener;

import com.chinalife.common.utils.JsonUtils;
import com.chinalife.manauth.service.ManauthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthListener {

    @Autowired
    private ManauthService manauthService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "manauth.auth.del.queue", durable = "true"),
            exchange = @Exchange(name = "cl.auth.exchange",type = ExchangeTypes.TOPIC),
            key = {"auth.delete"}
    ))
    public void listenDelete(String authId) {
        if (StringUtils.isBlank(authId)) {
            return;
        }
        Integer id = Integer.parseInt(authId);
        manauthService.delAuthFromClerks(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "manauth.auth.update.queue", durable = "true"),
            exchange = @Exchange(name = "cl.auth.exchange",type = ExchangeTypes.TOPIC),
            key = {"auth.update"}
    ))
    public void listenUpdate(String authId) {
        if (StringUtils.isBlank(authId)) {
            return;
        }
        Integer id = Integer.parseInt(authId);
        manauthService.updateClerkAuth(id);
    }

}
