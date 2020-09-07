package com.chinalife.search.listener;

import com.chinalife.search.service.SearchClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientListener {

    @Autowired
    private SearchClientService searchClientService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.client.insert.queue", durable = "true"),
            exchange = @Exchange(name = "cl.client.exchange",type = ExchangeTypes.TOPIC),
            key = {"client.insert","client.update"}
    ))
    public void listenInsertOrUpdate(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        searchClientService.insertOrUpdate(id);
    }
}
