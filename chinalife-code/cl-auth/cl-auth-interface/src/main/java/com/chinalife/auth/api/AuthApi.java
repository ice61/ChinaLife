package com.chinalife.auth.api;

import com.chinalife.auth.vo.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface AuthApi {
    @GetMapping("/auths")
    List<Auth> findAuthByIds(@RequestParam("ids") List<Integer> ids);

    @GetMapping("/all")
    List<Auth> findAll();

    @GetMapping("/findone/{authId}")
    Auth findAuthById(@PathVariable("authId") Integer authId);
}
