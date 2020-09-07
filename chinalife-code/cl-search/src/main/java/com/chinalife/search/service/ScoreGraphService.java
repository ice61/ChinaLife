package com.chinalife.search.service;


import java.util.List;
import java.util.Map;

public interface ScoreGraphService {
    Map<String,Long> scoreGraph(String from,String to,int interval,String token,String clerkId);

    List<Map<String,Long>> scoreRand(String from, String to, int num, Boolean desc);
}
