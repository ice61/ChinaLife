package com.chinalife.insurance.service;

import com.chinalife.client.po.Client;
import com.chinalife.insurance.po.Insurance;

import java.util.List;

public interface InsuranceService {
    void addInsurance(Insurance insurance);

    Insurance findInsByOrderId(Long orderId);

    Client findClientByOrderId(Long orderId);

    void update(Insurance insurance);

    void delete(List<Long> orderIds);
}
