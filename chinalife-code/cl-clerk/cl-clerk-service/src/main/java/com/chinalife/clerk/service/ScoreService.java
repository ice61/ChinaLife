package com.chinalife.clerk.service;

public interface ScoreService {

    Integer findScoreById(String id);

    void insert(String id);

    void delete(String id);

    void raise(String clerkId);

    void down(String clerkId);
}
