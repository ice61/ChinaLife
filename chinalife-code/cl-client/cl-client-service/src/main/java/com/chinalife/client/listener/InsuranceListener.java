package com.chinalife.client.listener;

import com.chinalife.client.client.InsuranceClient;
import com.chinalife.client.po.Client;
import com.chinalife.client.service.ClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InsuranceListener {

    @Autowired
    private ClientService clientService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "client.insert.queue", durable = "true"),
            exchange = @Exchange(name = "cl.insurance.exchange",type = ExchangeTypes.TOPIC),
            key = {"insurance.add.client","insurance.update.client"}
    ))
    public void listenInsertOrUpdate(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return;
        }
        Long id = Long.parseLong(orderId);
        clientService.updateClient(id);

    }
}
