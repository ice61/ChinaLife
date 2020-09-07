package com.chinalife.manauth.vo;

import com.chinalife.auth.vo.Auth;
import lombok.Data;

import java.util.List;

@Data
public class GroupWithAuth {

    private Integer id;

    private String alias;

    private List<Auth> auths;
}
