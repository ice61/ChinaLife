package com.chinalife.insurance.web;

import com.chinalife.client.po.Client;
import com.chinalife.insurance.po.Insurance;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InsuranceController {
    ResponseEntity<Void> addOrder(Insurance insurance);

    ResponseEntity<Insurance> findInsByOrderId(Long orderId);

    ResponseEntity<Client> findClientByOrderId(Long orderId);

    ResponseEntity<Void> updateOrder(Insurance insurance);

    ResponseEntity<Void> deleteOrder(List<Long> orderIds);
}
