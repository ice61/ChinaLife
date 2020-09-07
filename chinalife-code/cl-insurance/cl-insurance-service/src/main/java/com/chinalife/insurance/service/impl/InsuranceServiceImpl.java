package com.chinalife.insurance.service.impl;

import com.chinalife.client.po.Client;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.common.utils.IdWorker;
import com.chinalife.insurance.interceptor.ClerkInterceptor;
import com.chinalife.insurance.mapper.InsuranceMapper;
import com.chinalife.insurance.po.Insurance;
import com.chinalife.insurance.service.InsuranceService;
import com.chinalife.manauth.entity.ClerkInfo;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsuranceServiceImpl implements InsuranceService {

    @Autowired
    private InsuranceMapper insuranceMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public void addInsurance(Insurance insurance) {
        // 首先需要获取当前员工信息
        ClerkInfo clerkInfo = ClerkInterceptor.getClerkInfo();
        //添加前端未生成的值
        long orderId = idWorker.nextId();
        insurance.setOrderId(orderId);
        String clerkId = ClerkInterceptor.getClerkInfo().getId();
        insurance.setClerkId(clerkId);
        Date date = new Date();
        insurance.setCreated(date);
        insurance.setUpdated(date);

        insuranceMapper.insert(insurance);
        //员工微服务监听
        amqpTemplate.convertAndSend("cl.insurance.exchange","insurance.raise.score",clerkId);
        //搜索微服务监听
        amqpTemplate.convertAndSend("cl.insurance.exchange","insurance.insert",insurance.getOrderId());
        //客户微服务监听
        amqpTemplate.convertAndSend("cl.insurance.exchange","insurance.add.client",insurance.getOrderId());

    }

    @Override
    public Insurance findInsByOrderId(Long orderId) {
        Insurance insurance = insuranceMapper.selectByPrimaryKey(orderId);
        if (insurance == null) {
            throw new ClException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        return insurance;
    }

    @Override
    public Client findClientByOrderId(Long orderId) {
        Insurance insurance = insuranceMapper.selectByPrimaryKey(orderId);
        if (insurance == null) {
            throw new ClException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        Client client = new Client();
        client.setId(insurance.getClientId());
        client.setName(insurance.getClientName());
        client.setImage(insurance.getImage());
        client.setBirthday(insurance.getBirthday());
        client.setPhone(insurance.getPhone());
        client.setSex(insurance.getSex());
        return client;
    }

    @Override
    public void update(Insurance insurance) {
        Insurance oldInsurance = insuranceMapper.selectByPrimaryKey(insurance.getOrderId());
        if (oldInsurance == null) {
            throw new ClException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        oldInsurance.setBirthday(insurance.getBirthday());
        oldInsurance.setPhone(insurance.getPhone());
        oldInsurance.setImage(insurance.getImage());
        oldInsurance.setUpdated(new Date());
        insuranceMapper.updateByPrimaryKey(oldInsurance);
        //搜索微服务监听
        amqpTemplate.convertAndSend("cl.insurance.exchange","insurance.update",oldInsurance.getOrderId());
        //客户微服务监听
        amqpTemplate.convertAndSend("cl.insurance.exchange","insurance.update.client",oldInsurance.getOrderId());
    }

    @Override
    public void delete(List<Long> orderIds) {
        List<Insurance> insurances = insuranceMapper.selectByIdList(orderIds);
        if (CollectionUtils.isEmpty(insurances)) {
            throw new ClException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        List<String> clerkIds = insurances.stream().map(i -> i.getClerkId()).collect(Collectors.toList());
        insuranceMapper.deleteByIdList(orderIds);
        for (Long orderId : orderIds) {
            amqpTemplate.convertAndSend("cl.insurance.exchange","insurance.delete",orderId);
        }
        for (String clerkId : clerkIds) {
            amqpTemplate.convertAndSend("cl.insurance.exchange","insurance.down.score",clerkId);
        }
    }
}
