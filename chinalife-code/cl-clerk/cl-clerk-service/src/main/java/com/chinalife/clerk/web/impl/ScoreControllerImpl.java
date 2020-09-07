package com.chinalife.clerk.web.impl;

import com.chinalife.clerk.service.ScoreService;
import com.chinalife.clerk.web.ScoreController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScoreControllerImpl implements ScoreController {

    @Autowired
    private ScoreService scoreService;

    @Override
    @GetMapping("findScore/{id}")
    public ResponseEntity<Integer> findScoreById(@PathVariable("id") String id) {
        return ResponseEntity.ok(scoreService.findScoreById(id));
    }

}
