package com.chinalife.auth.service.impl;

import com.chinalife.auth.mapper.AuthGroupMapper;
import com.chinalife.auth.mapper.AuthMapper;
import com.chinalife.auth.service.AuthService;
import com.chinalife.auth.vo.Auth;
import com.chinalife.auth.vo.AuthGroup;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.common.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private AuthGroupMapper groupMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public List<Auth> findAuthByIds(List<Integer> ids) {
        List<Auth> auths = authMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(auths)) {
            throw new ClException(ExceptionEnum.AUTH_NOT_FOUND);
        }
        return auths;
    }

    @Override
    public List<Auth> findAll() {
        List<Auth> auths = authMapper.selectAll();
        if (CollectionUtils.isEmpty(auths)) {
            throw new ClException(ExceptionEnum.AUTH_NOT_FOUND);
        }
        return auths;

    }

    @Override
    public void addAuth(String alias, String url, Integer groupId) {
        AuthGroup authGroup = groupMapper.selectByPrimaryKey(groupId);
        if (authGroup == null) {
            throw new ClException(ExceptionEnum.AUTH_GROUP_NOT_FOUND);
        }

        Auth auth = new Auth();
        auth.setUrl(url);
        int i = authMapper.selectCount(auth);
        if (i == 1) {
            throw new ClException(ExceptionEnum.AUTH_EXIST_ALERDAY);
        }
        auth.setAlias(alias);
        auth.setGroupId(groupId);
        authMapper.insert(auth);
    }

    @Override
    public void delAuth(Integer id) {
        Auth auth = authMapper.selectByPrimaryKey(id);
        if (auth == null) {
            throw new ClException(ExceptionEnum.AUTH_NOT_FOUND);
        }
        authMapper.deleteByPrimaryKey(id);
        amqpTemplate.convertAndSend("cl.auth.exchange", "auth.delete", id);
    }

    @Override
    public List<Auth> findAuthByGroupId(Integer id) {
        Auth auth = new Auth();
        auth.setGroupId(id);
        List<Auth> auths = authMapper.select(auth);
        if (CollectionUtils.isEmpty(auths)) {
            throw new ClException(ExceptionEnum.AUTH_NOT_FOUND);
        }
        return auths;
    }

    @Override
    public void updateAuth(Auth auth) {
        Auth oldAuth = authMapper.selectByPrimaryKey(auth.getId());
        if (oldAuth == null) {
            throw new ClException(ExceptionEnum.AUTH_NOT_FOUND);
        }
        oldAuth.setAlias(auth.getAlias());
        oldAuth.setGroupId(auth.getGroupId());
        oldAuth.setUrl(auth.getUrl());
        authMapper.updateByPrimaryKey(oldAuth);
        amqpTemplate.convertAndSend("cl.auth.exchange","auth.update",oldAuth.getId());
    }

    @Override
    public Auth findAuthById(Integer authId) {
        Auth auth = authMapper.selectByPrimaryKey(authId);
        return auth;
    }

}
