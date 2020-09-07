package com.chinalife.search.service;

import com.chinalife.insurance.po.Insurance;
import com.chinalife.search.vo.InsurancePageResult;

import java.util.Date;

public interface SearchInsuranceService {

    void insertOrUpdateInsurance(Long orderId);

    InsurancePageResult search(Integer page, Integer rows, String sortBy,
                               Boolean desc, String key, String from, String to,String token,String clerkId);

    void deleteInsurance(Long id);
}
