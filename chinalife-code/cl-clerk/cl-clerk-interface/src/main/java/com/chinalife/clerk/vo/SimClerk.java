package com.chinalife.clerk.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class SimClerk {

    private String id;
    private String instiution;
    private String name;
    private Boolean sex;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private String phone;
    private String image;
    private String password;

}
