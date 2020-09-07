package com.chinalife.search.web.impl;

import com.chinalife.search.po.SearchClient;
import com.chinalife.search.service.SearchClientService;
import com.chinalife.search.vo.ClientPageResult;
import com.chinalife.search.web.SearchClientController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchClientControllerImpl implements SearchClientController {

    @Autowired
    private SearchClientService searchClientService;

    @Override
    @GetMapping("/client/{clientId}")
    public ResponseEntity<SearchClient> findClientById(@PathVariable("clientId") String clientId) {
        return ResponseEntity.ok(searchClientService.findClientById(clientId));
    }

    @Override
    @GetMapping("/client/page")
    public ResponseEntity<ClientPageResult> search(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
            @RequestParam(value = "key",required = false) String key
    ) {
        return ResponseEntity.ok(searchClientService.search(page,rows, sortBy,desc,key));
    }
}
