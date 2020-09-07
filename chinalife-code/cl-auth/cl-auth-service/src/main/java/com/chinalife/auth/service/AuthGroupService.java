package com.chinalife.auth.service;

import com.chinalife.auth.vo.AuthGroup;
import com.chinalife.auth.vo.AuthGroupAll;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AuthGroupService {

    List<AuthGroup> findAuthGroup();

    void addAuthGroup(Integer id, String alias);

    List<AuthGroup> findAuthGroupByIds(List<Integer> ids);

    List<AuthGroupAll> findAuthGroupAll();
}
