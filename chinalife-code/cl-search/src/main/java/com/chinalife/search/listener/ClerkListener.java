package com.chinalife.search.listener;

import com.chinalife.search.service.SearchClerkService;
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
    private SearchClerkService searchClerkService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.clerk.insert.queue", durable = "true"),
            exchange = @Exchange(name = "cl.clerk.exchange",type = ExchangeTypes.TOPIC),
            key = {"clerk.update","clerk.insert","clerk.password.update"}
    ))
    public void listenInsertOrUpdate(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        searchClerkService.insertOrUpdateClerk(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.clerk.delete.queue", durable = "true"),
            exchange = @Exchange(name = "cl.clerk.exchange",type = ExchangeTypes.TOPIC),
            key = {"clerk.delete"}
    ))
    public void listenDelete(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        searchClerkService.deleteClerk(id);
    }

}
