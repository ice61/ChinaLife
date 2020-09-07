package com.chinalife.search.web;

import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.search.po.Clerk;
import com.chinalife.search.vo.ClerkPageResult;
import org.springframework.http.ResponseEntity;

public interface SearchClerkController {
    ResponseEntity<String> findImageById(String id);
    ResponseEntity<ClerkPageResult> search(Integer page,Integer rows,String sortBy,Boolean desc,String key);
    ResponseEntity<Clerk> findClerkById(String id);
}
