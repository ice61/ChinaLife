package com.chinalife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ClPanApplication {
    public static void main(String[] args) {
        //es与redis同时引入使用会导致初始化错误，无法启动。加入下面这一行代码
        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(ClPanApplication.class);
    }
}
