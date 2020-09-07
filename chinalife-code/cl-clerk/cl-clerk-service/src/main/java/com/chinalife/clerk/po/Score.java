package com.chinalife.clerk.po;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "achievement")
public class Score {

    @Id
    private String clerkId;
    private Integer score;
}
