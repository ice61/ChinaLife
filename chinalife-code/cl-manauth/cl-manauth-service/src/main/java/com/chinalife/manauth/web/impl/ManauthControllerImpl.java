package com.chinalife.manauth.web.impl;

import com.chinalife.auth.vo.Auth;
import com.chinalife.common.utils.CookieUtils;
import com.chinalife.manauth.config.JwtProperties;
import com.chinalife.manauth.entity.ClerkInfo;
import com.chinalife.manauth.po.ClerkAuth;
import com.chinalife.manauth.service.ManauthService;
import com.chinalife.manauth.vo.ClerkAuthPageResult;
import com.chinalife.manauth.vo.GroupWithAuth;
import com.chinalife.manauth.web.ManauthController;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class ManauthControllerImpl implements ManauthController {

    @Autowired
    private ManauthService manauthService;

    @Autowired
    private JwtProperties prop;


    @Override
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam("id") String id, @RequestParam("password") String password,
            @RequestParam("code") String code, HttpServletResponse response, HttpServletRequest request) {
        String token = manauthService.login(id, password, code);
        // 将生成的信息token写入cookie中
        CookieUtils.setCookie(request, response, prop.getCookieName(), token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue("CL_TOKEN") String token, HttpServletRequest request, HttpServletResponse response) {
        manauthService.logout(token, request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping("/verify")
    public ResponseEntity<ClerkInfo> verify(
            @CookieValue("CL_TOKEN") String token, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(manauthService.verify(token, response, request));
    }


    @Override
    @PostMapping("/add")
    public ResponseEntity<Void> addAuthToClerk(@RequestParam("id") String id, @RequestParam("ids") List<Integer> ids) {
        manauthService.addAuthToClerk(id, ids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PostMapping("/del")
    public ResponseEntity<Void> delAuthFromClerk(@RequestParam("id") String id, @RequestParam("ids") List<Integer> ids) {
        manauthService.delAuthFromClerk(id, ids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping("/page")
    public ResponseEntity<ClerkAuthPageResult> findAuthPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "time", defaultValue = "false") Boolean time
    ) {

        return ResponseEntity.ok(manauthService.findAuthPage(page, rows, sortBy, desc, key, time));
    }

    @Override
    @GetMapping("/choose/{id}")
    public ResponseEntity<Map<String, List<GroupWithAuth>>> findHaveAndWithout(@PathVariable("id") String id) {
        return ResponseEntity.ok(manauthService.findHaveAndWithout(id));
    }

    @Override
    @GetMapping("/find/person")
    public ResponseEntity<List<ClerkAuth>> findAuthByClerkId(@CookieValue("CL_TOKEN") String token) {
        return ResponseEntity.ok(manauthService.findAuthByClerkId(token));
    }

}
