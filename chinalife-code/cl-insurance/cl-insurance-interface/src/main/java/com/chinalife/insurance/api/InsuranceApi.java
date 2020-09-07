package com.chinalife.insurance.api;

import com.chinalife.client.po.Client;
import com.chinalife.insurance.po.Insurance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface InsuranceApi {

    @GetMapping("find/{orderId}")
    Insurance findInsByOrderId(@PathVariable("orderId") Long orderId);

    @GetMapping("client/{orderId}")
    Client findClientByOrderId(@PathVariable("orderId") Long orderId);
}
