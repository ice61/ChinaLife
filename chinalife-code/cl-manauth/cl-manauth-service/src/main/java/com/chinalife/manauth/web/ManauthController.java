package com.chinalife.manauth.web;

import com.chinalife.auth.vo.Auth;
import com.chinalife.manauth.entity.ClerkInfo;
import com.chinalife.manauth.po.ClerkAuth;
import com.chinalife.manauth.vo.ClerkAuthPageResult;
import com.chinalife.manauth.vo.GroupWithAuth;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ManauthController {

    ResponseEntity<Void> login(String id, String password,
                               String code, HttpServletResponse response, HttpServletRequest request);

    ResponseEntity<Void> logout(String token, HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<ClerkInfo> verify(String token, HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<Void> addAuthToClerk(String id, List<Integer> ids);

    ResponseEntity<Void> delAuthFromClerk(String id, List<Integer> ids);

    ResponseEntity<ClerkAuthPageResult> findAuthPage(Integer page, Integer rows, String sortBy, Boolean desc, String key, Boolean time);

    ResponseEntity<Map<String, List<GroupWithAuth>>> findHaveAndWithout(String id);

    ResponseEntity<List<ClerkAuth>> findAuthByClerkId(String token);

}
