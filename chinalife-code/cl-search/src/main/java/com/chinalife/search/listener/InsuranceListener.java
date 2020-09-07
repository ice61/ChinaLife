package com.chinalife.search.listener;

import com.chinalife.search.service.SearchInsuranceService;
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
    private SearchInsuranceService searchInsuranceService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.insurance.insert.queue", durable = "true"),
            exchange = @Exchange(name = "cl.insurance.exchange",type = ExchangeTypes.TOPIC),
            key = {"insurance.insert","insurance.update"}
    ))
    public void listenInsertOrUpdate(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return;
        }
        Long id = Long.parseLong(orderId);
        searchInsuranceService.insertOrUpdateInsurance(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.insurance.delete.queue", durable = "true"),
            exchange = @Exchange(name = "cl.insurance.exchange",type = ExchangeTypes.TOPIC),
            key = {"insurance.delete"}
    ))
    public void listenDelete(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return;
        }
        Long id = Long.parseLong(orderId);
        searchInsuranceService.deleteInsurance(id);
    }
}
