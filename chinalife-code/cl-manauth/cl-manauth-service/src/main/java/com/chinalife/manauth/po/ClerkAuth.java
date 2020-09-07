package com.chinalife.manauth.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "auth_give")
public class ClerkAuth {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    private String clerkId;

    private Integer authId;

    private String authAlias;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date time;
}
