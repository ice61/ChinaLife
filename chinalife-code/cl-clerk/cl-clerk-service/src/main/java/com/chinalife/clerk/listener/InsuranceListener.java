package com.chinalife.clerk.listener;

import com.chinalife.clerk.po.Score;
import com.chinalife.clerk.service.ScoreService;
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
    private ScoreService scoreService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "clerk.score.insert.queue", durable = "true"),
            exchange = @Exchange(name = "cl.insurance.exchange",type = ExchangeTypes.TOPIC),
            key = {"insurance.raise.score"}
    ))
    public void listenInsertOrUpdate(String clerkId) {
        if (StringUtils.isBlank(clerkId)) {
            return;
        }
        scoreService.raise(clerkId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "clerk.score.delete.queue", durable = "true"),
            exchange = @Exchange(name = "cl.insurance.exchange",type = ExchangeTypes.TOPIC),
            key = {"insurance.down.score"}
    ))
    public void listenDelete(String clerkId) {
        if (StringUtils.isBlank(clerkId)) {
            return;
        }
        scoreService.down(clerkId);
    }

}
