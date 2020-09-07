package com.chinalife.search.web;

import com.chinalife.search.po.SearchClient;
import com.chinalife.search.vo.ClientPageResult;
import org.springframework.http.ResponseEntity;

public interface SearchClientController {
    ResponseEntity<SearchClient> findClientById(String clientId);

    ResponseEntity<ClientPageResult> search(Integer page,Integer rows,String sortBy,Boolean desc,String key);
}
