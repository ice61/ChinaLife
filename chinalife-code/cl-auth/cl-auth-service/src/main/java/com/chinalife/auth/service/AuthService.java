package com.chinalife.auth.service;

import com.chinalife.auth.vo.Auth;

import java.util.List;

public interface AuthService {
    List<Auth> findAuthByIds(List<Integer> ids);

    List<Auth> findAll();

    void addAuth(String alias, String url, Integer groupId);

    void delAuth(Integer id);

    List<Auth> findAuthByGroupId(Integer id);

    void updateAuth(Auth auth);

    Auth findAuthById(Integer authId);
}
