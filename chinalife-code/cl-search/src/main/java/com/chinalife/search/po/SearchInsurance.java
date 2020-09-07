package com.chinalife.search.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "insurance",type = "docs",shards = 1,replicas = 0)
public class SearchInsurance {

    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    @Field(type = FieldType.Keyword)
    private String clientId;

    @Field(type = FieldType.Keyword)
    private String clientName;

    @Field(type = FieldType.Keyword)
    private String sex;

    @Field(type = FieldType.Keyword)
    private String phone;

    @Field(type = FieldType.Keyword,index = false)
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @Field(type = FieldType.Date)
    private Date birthday;      //前端传

    @Field(type = FieldType.Keyword)
    private String sort;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double money;

    @Field(type = FieldType.Keyword)
    private String clientfId;

    @Field(type = FieldType.Keyword)
    private String clientfName;

    @Field(type = FieldType.Keyword)
    private String clientsId;

    @Field(type = FieldType.Keyword)
    private String clientsName;

    @Field(type = FieldType.Keyword)
    private String clerkId;     //后端获取

    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date created;       //后端获取

    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updated;       //后端获取

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all;
}
