package com.chinalife.client.service;

import com.chinalife.client.po.Client;

public interface ClientService {
    void updateClient(Long id);

    Client findClientById(String id);
}
