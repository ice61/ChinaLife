package com.chinalife.search.service;

import com.chinalife.search.po.SearchClient;
import com.chinalife.search.vo.ClientPageResult;

public interface SearchClientService {

    void insertOrUpdate(String id);

    SearchClient findClientById(String clientId);

    ClientPageResult search(Integer page, Integer rows, String sortBy, Boolean desc, String key);
}
