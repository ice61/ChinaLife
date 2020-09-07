package com.chinalife.client.web;

import com.chinalife.client.po.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

public interface ClientController {

    ResponseEntity<Client> findClientById(String id);
}
