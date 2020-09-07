package com.chinalife.manauth.service;

import com.chinalife.manauth.entity.ClerkInfo;
import com.chinalife.manauth.po.ClerkAuth;
import com.chinalife.manauth.vo.ClerkAuthPageResult;
import com.chinalife.manauth.vo.GroupWithAuth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ManauthService {
    String login(String id, String password, String code);

    void logout(String token, HttpServletRequest request, HttpServletResponse response);

    ClerkInfo verify(String token, HttpServletResponse response, HttpServletRequest request);

    void addAuthToClerk(String id, List<Integer> ids);

    void delAuthFromClerk(String id, List<Integer> ids);

    ClerkAuthPageResult findAuthPage(Integer page, Integer rows, String sortBy, Boolean desc, String key, Boolean time);

    Map<String, List<GroupWithAuth>> findHaveAndWithout(String id);

    void delAuthFromClerks(Integer id);

    List<ClerkAuth> findAuthByClerkId(String token);

    void updateToken(String id);

    void delToken(String clerkId);

    void updateClerkAuth(Integer authId);
}
