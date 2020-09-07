package com.chinalife.clerk.web;

import org.springframework.http.ResponseEntity;

public interface ScoreController {

    ResponseEntity<Integer> findScoreById(String id);
}
