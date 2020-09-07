package com.chinalife.manauth.entity;

import com.chinalife.auth.vo.AuthGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClerkInfo {
    // 员工工号
    private String id;
    // 员工所属机构号
    private String instiution;
    // 员工姓名
    private String name;
    // 员工头像
    private String image;

    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updated;

    // 员工可访问权限模块
    //private Set<Integer> authModel;
    private List<AuthGroup> authModel;
}
