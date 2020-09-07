package com.chinalife.clerk.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ScoreApi {

    @GetMapping("findScore/{id}")
    Integer findScoreById(@PathVariable("id") String id);
}
