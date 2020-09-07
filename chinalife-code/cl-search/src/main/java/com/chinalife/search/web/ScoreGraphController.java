package com.chinalife.search.web;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ScoreGraphController {
    ResponseEntity<Map<String, Long>> scoreGraphAll(String from, String to,int interval);

    ResponseEntity<Map<String,Long>> scoreGraphClerk(String from,String to,String clerkId,int interval);

    ResponseEntity<Map<String,Long>> scoreGraphPerson(String from,String to,String token,int interval);

    ResponseEntity<List<Map<String,Long>>> scoreRank(String from, String to, int num, Boolean desc);

}
