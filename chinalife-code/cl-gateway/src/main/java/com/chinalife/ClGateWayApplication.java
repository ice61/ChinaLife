package com.chinalife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ClGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClGateWayApplication.class);
    }
}
