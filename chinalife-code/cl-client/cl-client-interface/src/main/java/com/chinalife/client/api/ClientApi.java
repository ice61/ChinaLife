package com.chinalife.client.api;

import com.chinalife.client.po.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ClientApi {

    @GetMapping("find/{id}")
    Client findClientById(@PathVariable("id") String id);

}
