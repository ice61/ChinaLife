package com.chinalife.clerk.web;

import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.clerk.vo.SimClerk;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

public interface ClerkController {
    ResponseEntity<Boolean> checkData(String id);

    ResponseEntity<SearchClerk> findClerkById(String id);

    ResponseEntity<Void> sendCode(String phone);

    ResponseEntity<Void> register(SimClerk simClerk, String code);

    ResponseEntity<Void> deleteClerkById(String id);

    ResponseEntity<SearchClerk> query(String id, String password, String code);

    void imageCode(HttpServletResponse response, String id);

    ResponseEntity<Void> updateClerk(SimClerk simClerk);

    ResponseEntity<Void> updateClerkPerson(SimClerk simClerk, String token);

    ResponseEntity<Void> updatePassword(String clerkId, String oldPassword, String newPassword);

    ResponseEntity<Void> updatePasswordByCode(String clerkId, String newPassword,String code,String phone);

    ResponseEntity<Void> sendCodeUP(String phone);
}
