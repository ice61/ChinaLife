package com.chinalife.es;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "clerk")
public class Clerk {

    @Id
    private String id;
    private String instiution;
    private String name;
    private Boolean sex;
    private Date birthday;
    private String phone;
    private String image;
    private String password;
    private String salt;
    private Date created;
    private Date updated;
}
