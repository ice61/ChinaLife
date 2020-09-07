package com.chinalife.manauth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClerkAuthInfo {
    // 员工工号
    private String id;
    // 员工权限
    private List<String> auths;
}
