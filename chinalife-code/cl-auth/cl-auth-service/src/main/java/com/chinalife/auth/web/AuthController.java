package com.chinalife.auth.web;

import com.chinalife.auth.vo.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

public interface AuthController {

    ResponseEntity<List<Auth>> findAuthByIds(List<Integer> ids);

    ResponseEntity<List<Auth>> findAll();

    ResponseEntity<Void> addAuth(String alias, String url, Integer groupId);

    ResponseEntity<Void> delAuth(Integer id);

    ResponseEntity<List<Auth>> findAuthByGroupId(Integer id);

    ResponseEntity<Void> updateAuth(Auth auth);

    ResponseEntity<Auth> findAuthById(Integer authId);
}
