package com.chinalife.clerk.service;

import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.clerk.vo.SimClerk;

import java.awt.image.BufferedImage;

public interface ClerkService {
    Boolean checkData(String id);

    void sendCode(String phone);

    void register(SimClerk simClerk, String code);

    SearchClerk findClerkById(String id);

    void deleteClerkById(String id);

    BufferedImage imageCode(String id);

    SearchClerk query(String id, String password, String code);

    void updateClerk(SimClerk simClerk,String token);

    void sendCodeUP(String phone);

    void updatePassword(String clerkId,String oldPassword,String newPassword,String code,String phone);
}
