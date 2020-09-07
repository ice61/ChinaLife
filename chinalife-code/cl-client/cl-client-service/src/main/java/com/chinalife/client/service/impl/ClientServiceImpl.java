package com.chinalife.client.service.impl;

import com.chinalife.client.client.InsuranceClient;
import com.chinalife.client.mapper.ClientMapper;
import com.chinalife.client.po.Client;
import com.chinalife.client.service.ClientService;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.insurance.po.Insurance;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private InsuranceClient insuranceClient;

    @Override
    public void updateClient(Long orderId) {
        Client client = insuranceClient.findClientByOrderId(orderId);
        Client client1 = clientMapper.selectByPrimaryKey(client.getId());
        if (client1 == null) {
            clientMapper.insert(client);
            amqpTemplate.convertAndSend("cl.client.exchange","client.insert",client.getId());
            return;
        }
        if (!client.equals(client1)) {
            clientMapper.updateByPrimaryKey(client);
            amqpTemplate.convertAndSend("cl.client.exchange","client.update",client.getId());
        }
    }

    @Override
    public Client findClientById(String id) {
        Client client = clientMapper.selectByPrimaryKey(id);
        if (client == null) {
            throw new ClException(ExceptionEnum.CLIENT_NOT_FOUND);
        }
        return client;

    }
}
