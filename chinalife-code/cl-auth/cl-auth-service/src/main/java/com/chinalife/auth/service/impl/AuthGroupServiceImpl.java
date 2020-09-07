package com.chinalife.auth.service.impl;

import com.chinalife.auth.mapper.AuthGroupMapper;
import com.chinalife.auth.mapper.AuthMapper;
import com.chinalife.auth.service.AuthGroupService;
import com.chinalife.auth.vo.Auth;
import com.chinalife.auth.vo.AuthGroup;
import com.chinalife.auth.vo.AuthGroupAll;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@Service
public class AuthGroupServiceImpl implements AuthGroupService {

    @Autowired
    private AuthGroupMapper authGroupMapper;

    @Autowired
    private AuthMapper authMapper;

    @Override
    public List<AuthGroup> findAuthGroup() {
        List<AuthGroup> authGroups = authGroupMapper.selectAll();
        if (CollectionUtils.isEmpty(authGroups)) {
            throw new ClException(ExceptionEnum.AUTH_GROUP_NOT_FOUND);
        }
        return authGroups;
    }

    @Override
    public void addAuthGroup(Integer id, String alias) {
        AuthGroup authGroup = authGroupMapper.selectByPrimaryKey(id);
        if (authGroup != null) {
            throw new ClException(ExceptionEnum.AUTH_GROUP_EXIST_ALERDAY);
        }
        AuthGroup group = new AuthGroup();
        group.setId(id);
        group.setAlias(alias);
        authGroupMapper.insert(group);
    }

    @Override
    public List<AuthGroup> findAuthGroupByIds(List<Integer> ids) {
        List<AuthGroup> authGroups = authGroupMapper.selectByIdList(ids);
        return authGroups;
    }

    @Override
    public List<AuthGroupAll> findAuthGroupAll() {
        List<AuthGroupAll> authGroupAlls = new LinkedList<>();
        List<AuthGroup> authGroups = authGroupMapper.selectAll();
        for (AuthGroup authGroup : authGroups) {
            Auth auth = new Auth();
            auth.setGroupId(authGroup.getId());
            List<Auth> auths = authMapper.select(auth);
            if (!CollectionUtils.isEmpty(auths)) {
                AuthGroupAll authGroupAll = new AuthGroupAll();
                authGroupAll.setId(authGroup.getId());
                authGroupAll.setAlias(authGroup.getAlias());
                authGroupAll.setChildren(auths);
                authGroupAlls.add(authGroupAll);
            }
        }
        if (CollectionUtils.isEmpty(authGroupAlls)) {
            throw new ClException(ExceptionEnum.AUTH_NOT_FOUND);
        }
        return authGroupAlls;
    }
}
