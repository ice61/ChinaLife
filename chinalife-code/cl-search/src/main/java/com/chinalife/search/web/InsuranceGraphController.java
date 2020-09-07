package com.chinalife.search.web;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface InsuranceGraphController {
    ResponseEntity<Map<String, Long>> sortGraphAll(String from, String to);

    ResponseEntity<Map<String,Long>> sortGraphClerk(String from,String to,String clerkId);

    ResponseEntity<Map<String,Long>> sortGraphPersosn(String from,String to,String token);
}
