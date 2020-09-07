package com.chinalife.insurance.web.impl;

import com.chinalife.client.po.Client;
import com.chinalife.insurance.po.Insurance;
import com.chinalife.insurance.service.InsuranceService;
import com.chinalife.insurance.web.InsuranceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InsuranceControllerImpl implements InsuranceController {

    @Autowired
    private InsuranceService insuranceService;

    @Override
    @PostMapping("/add")
    public ResponseEntity<Void> addOrder(Insurance insurance) {
        insuranceService.addInsurance(insurance);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping("/find/{orderId}")
    public ResponseEntity<Insurance> findInsByOrderId(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(insuranceService.findInsByOrderId(orderId));
    }

    @Override
    @GetMapping("/client/{orderId}")
    public ResponseEntity<Client> findClientByOrderId(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(insuranceService.findClientByOrderId(orderId));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<Void> updateOrder(@RequestBody Insurance insurance) {
        insuranceService.update(insurance);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteOrder(@RequestParam("orderIds") List<Long> orderIds) {
        insuranceService.delete(orderIds);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
