package com.chinalife.clerk.web.impl;

import com.chinalife.clerk.mapper.ClerkMapper;
import com.chinalife.clerk.po.Clerk;
import com.chinalife.clerk.service.ClerkService;
import com.chinalife.clerk.utils.ImageCode;
import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.clerk.vo.SimClerk;
import com.chinalife.clerk.web.ClerkController;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class ClerkControllerImpl implements ClerkController {

    @Autowired
    private ClerkService clerkService;

    @Override
    @GetMapping("/check/{id}")
    public ResponseEntity<Boolean> checkData(@PathVariable("id") String id) {
        return ResponseEntity.ok(clerkService.checkData(id));
    }

    @Override
    @GetMapping("/find/{id}")
    public ResponseEntity<SearchClerk> findClerkById(@PathVariable("id") String id) {
        return ResponseEntity.ok(clerkService.findClerkById(id));
    }


    @Override
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone) {
        clerkService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<Void> register(SimClerk simClerk, @RequestParam("code") String code) {
        clerkService.register(simClerk, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @DeleteMapping("/del/{id}")
    public ResponseEntity<Void> deleteClerkById(@PathVariable("id") String id) {
        clerkService.deleteClerkById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @Override
    @GetMapping("/query")
    public ResponseEntity<SearchClerk> query(@RequestParam("id") String id,
                                             @RequestParam("password") String password,
                                             @RequestParam("code") String code) {
        return ResponseEntity.ok(clerkService.query(id, password, code));
    }

    @Override
    @GetMapping("/image/code/{id}")
    public void imageCode(HttpServletResponse response, @PathVariable("id") String id) {
        try {
            ImageCode.output(clerkService.imageCode(id), response.getOutputStream());
        } catch (IOException e) {
            throw new ClException(ExceptionEnum.RETURN_IMAGE_CODE_ERROR);
        }
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<Void> updateClerk(@RequestBody SimClerk simClerk) {
        clerkService.updateClerk(simClerk, null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PutMapping("/update/person")
    public ResponseEntity<Void> updateClerkPerson(@RequestBody SimClerk simClerk, @CookieValue("CL_TOKEN") String token) {
        clerkService.updateClerk(simClerk, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PostMapping("/change/password/old")
    public ResponseEntity<Void> updatePassword(@RequestParam("clerkId") String clerkId,
                                               @RequestParam("oldPassword") String oldPassword,
                                               @RequestParam("newPassword") String newPassword) {
        clerkService.updatePassword(clerkId, oldPassword, newPassword, null, null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PostMapping("/change/password/code")
    public ResponseEntity<Void> updatePasswordByCode(
            @RequestParam("clerkId") String clerkId,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("code") String code,
            @RequestParam("phone") String phone) {
        clerkService.updatePassword(clerkId, null, newPassword, code, phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PostMapping("/password/code")
    public ResponseEntity<Void> sendCodeUP(@RequestParam("phone") String phone) {
        clerkService.sendCodeUP(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
