package com.chinalife.auth.web.impl;

import com.chinalife.auth.service.AuthGroupService;
import com.chinalife.auth.vo.AuthGroup;
import com.chinalife.auth.vo.AuthGroupAll;
import com.chinalife.auth.web.AuthGroupController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthGroupControllerImpl implements AuthGroupController {

    @Autowired
    private AuthGroupService authGroupService;

    @Override
    @PostMapping("/add/group")
    public ResponseEntity<Void> addAuthGroup(@RequestParam("id") Integer id,@RequestParam("alias") String alias) {
        authGroupService.addAuthGroup(id,alias);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping("/find/group")
    public ResponseEntity<List<AuthGroup>> findAuthGroup() {
        return ResponseEntity.ok(authGroupService.findAuthGroup());
    }

    @Override
    @GetMapping("/find/groups")
    public ResponseEntity<List<AuthGroup>> findAuthGroupByIds(@RequestParam("ids") List<Integer> ids) {
        return ResponseEntity.ok(authGroupService.findAuthGroupByIds(ids));
    }

    @Override
    @GetMapping("/find/groupAll")
    public ResponseEntity<List<AuthGroupAll>> findAuthGroupAll() {
        return ResponseEntity.ok(authGroupService.findAuthGroupAll());
    }


}
