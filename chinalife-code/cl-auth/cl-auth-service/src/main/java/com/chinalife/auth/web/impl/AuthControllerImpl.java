package com.chinalife.auth.web.impl;

import com.chinalife.auth.service.AuthService;
import com.chinalife.auth.vo.Auth;
import com.chinalife.auth.web.AuthController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthControllerImpl implements AuthController {

    @Autowired
    private AuthService authService;

    @Override
    @GetMapping("/auths")
    public ResponseEntity<List<Auth>> findAuthByIds(@RequestParam("ids") List<Integer> ids) {
        return ResponseEntity.ok(authService.findAuthByIds(ids));
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<List<Auth>> findAll() {
        return ResponseEntity.ok(authService.findAll());
    }


    @Override
    @PostMapping("/add")
    public ResponseEntity<Void> addAuth(
            @RequestParam("alias") String alias, @RequestParam("url") String url, @RequestParam("groupId") Integer groupId) {
        authService.addAuth(alias,url,groupId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Override
    @DeleteMapping("/del/{id}")
    public ResponseEntity<Void> delAuth(@PathVariable("id") Integer id) {
        authService.delAuth(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping("/find")
    public ResponseEntity<List<Auth>> findAuthByGroupId(@RequestParam("groupId") Integer id) {
        return ResponseEntity.ok(authService.findAuthByGroupId(id));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<Void> updateAuth(@RequestBody Auth auth) {
        authService.updateAuth(auth);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping("/findone/{authId}")
    public ResponseEntity<Auth> findAuthById(@PathVariable("authId") Integer authId) {
        return ResponseEntity.ok(authService.findAuthById(authId));
    }

}
