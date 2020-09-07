package com.chinalife.search.web;

import com.chinalife.search.vo.InsurancePageResult;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface SearchInsuranceController {
    ResponseEntity<InsurancePageResult> search(
            Integer page, Integer rows, String sortBy, Boolean desc, String key, String from, String to);

    ResponseEntity<InsurancePageResult> searchByPerson(
            Integer page, Integer rows, String sortBy, Boolean desc, String key, String from, String to,String token);

    ResponseEntity<InsurancePageResult> searchByClerkId(
            Integer page, Integer rows, String sortBy, Boolean desc, String key, String from, String to,String clerkId);
}
