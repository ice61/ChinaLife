package com.chinalife.search.service;

import java.util.Map;

public interface InsuranceGraphService {

    Map<String,Long> sortGraph(String from,String to,String token,String clerkId);
}
