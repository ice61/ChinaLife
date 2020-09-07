package com.chinalife.client.web.impl;

import com.chinalife.client.po.Client;
import com.chinalife.client.service.ClientService;
import com.chinalife.client.web.ClientController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientControllerImpl implements ClientController {

    @Autowired
    private ClientService clientService;

    @Override
    @GetMapping("find/{id}")
    public ResponseEntity<Client> findClientById(@PathVariable("id") String id) {
        return ResponseEntity.ok(clientService.findClientById(id));
    }
}
