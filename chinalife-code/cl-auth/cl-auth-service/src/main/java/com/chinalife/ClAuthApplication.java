package com.chinalife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.chinalife.auth.mapper")
public class ClAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClAuthApplication.class);
    }
}
