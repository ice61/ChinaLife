package com.chinalife.clerk.service.impl;

import com.chinalife.clerk.mapper.ScoreMapper;
import com.chinalife.clerk.po.Score;
import com.chinalife.clerk.service.ClerkService;
import com.chinalife.clerk.service.ScoreService;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public Integer findScoreById(String id) {
        Score score = new Score();
        score.setClerkId(id);
        Score result = scoreMapper.selectOne(score);
        if(result == null) {
            throw new ClException(ExceptionEnum.FIND_CLERK_FAIL);
        }
        return result.getScore();
    }

    @Override
    public void insert(String id) {
        Score score = new Score();
        score.setClerkId(id);
        score.setScore(0);
        scoreMapper.insert(score);
    }

    @Override
    public void delete(String id) {
        Score score = scoreMapper.selectByPrimaryKey(id);
        if (score == null) {
            throw new ClException(ExceptionEnum.FIND_CLERK_SCORE_FAIL);
        }
        int i = scoreMapper.delete(score);
        if (i != 1) {
            throw new ClException(ExceptionEnum.DELETE_CLERK_SCORE_ERROR);
        }
    }

    @Override
    public void raise(String clerkId) {
        Score score = scoreMapper.selectByPrimaryKey(clerkId);
        if (score == null) {
            throw new ClException(ExceptionEnum.FIND_CLERK_SCORE_FAIL);
        }
        score.setScore(score.getScore() + 1);
        scoreMapper.updateByPrimaryKey(score);
        amqpTemplate.convertAndSend("cl.clerk.exchange", "clerk.update", clerkId);
    }

    @Override
    public void down(String clerkId) {
        Score score = scoreMapper.selectByPrimaryKey(clerkId);
        if (score == null) {
            throw new ClException(ExceptionEnum.FIND_CLERK_SCORE_FAIL);
        }
        score.setScore(score.getScore() -1);
        scoreMapper.updateByPrimaryKey(score);
        amqpTemplate.convertAndSend("cl.clerk.exchange", "clerk.update", clerkId);
    }
}
