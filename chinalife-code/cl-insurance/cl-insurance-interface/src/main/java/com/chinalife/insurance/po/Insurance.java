package com.chinalife.insurance.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "insurance")
public class Insurance {
    @Id
    private Long orderId;       //自己生成
    private String clientId;
    private String clientName;
    private Boolean sex;
    private String phone;
    private String image;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date birthday;      //前端传
    private String sort;
    private Double money;
    private String clientfId;
    private String clientfName;
    private String clientsId;
    private String clientsName;
    private String clerkId;     //后端获取
    private Date created;       //后端获取
    private Date updated;       //后端获取
}
