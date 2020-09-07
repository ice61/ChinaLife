package com.chinalife.auth.vo;

import lombok.Data;

import java.util.List;

@Data
public class AuthGroupAll {
    private Integer id;

    private String alias;

    private List<Auth> children;
}
