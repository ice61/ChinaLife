package com.chinalife.search.web.impl;

import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.search.po.Clerk;
import com.chinalife.search.service.SearchClerkService;
import com.chinalife.search.vo.ClerkPageResult;
import com.chinalife.search.web.SearchClerkController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.SocketTimeoutException;

@RestController
public class SearchClerkControllerImpl implements SearchClerkController {

    @Autowired
    private SearchClerkService searchClerkService;

    @Override
    @GetMapping("/image/{id}")
    public ResponseEntity<String> findImageById(@PathVariable("id") String id) {
        return ResponseEntity.ok(searchClerkService.findImageById(id));
    }

    @Override
    @GetMapping("/clerk/page")
    public ResponseEntity<ClerkPageResult> search(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
            @RequestParam(value = "key",required = false) String key
    ) {
        return ResponseEntity.ok(searchClerkService.search(page,rows, sortBy,desc,key));
    }

    @Override
    @GetMapping("/clerk/one/{id}")
    public ResponseEntity<Clerk> findClerkById(@PathVariable("id") String id) {
        return ResponseEntity.ok(searchClerkService.findClerkById(id));
    }


}
