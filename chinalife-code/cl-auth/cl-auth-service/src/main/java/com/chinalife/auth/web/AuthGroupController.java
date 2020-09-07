package com.chinalife.auth.web;

import com.chinalife.auth.vo.AuthGroup;
import com.chinalife.auth.vo.AuthGroupAll;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AuthGroupController {

    ResponseEntity<Void> addAuthGroup(Integer id, String alias);

    ResponseEntity<List<AuthGroup>> findAuthGroup();

    //ResponseEntity<Void> delAuthGroup(Integer id);

    ResponseEntity<List<AuthGroup>> findAuthGroupByIds(List<Integer> ids);

    ResponseEntity<List<AuthGroupAll>> findAuthGroupAll();
}
