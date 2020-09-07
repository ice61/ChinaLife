package com.chinalife.clerk.api;

import com.chinalife.clerk.vo.SearchClerk;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface ClerkApi {

    @GetMapping("/find/{id}")
    SearchClerk findClerkById(@PathVariable("id") String id);

    @GetMapping("/query")
    SearchClerk query(@RequestParam("id") String id,
                      @RequestParam("password") String password, @RequestParam("code") String code);

}
