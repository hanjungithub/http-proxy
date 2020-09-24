package com.example.httpproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HttpProxyApplication {

    private static Logger logger = LoggerFactory.getLogger(HttpProxyApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(HttpProxyApplication.class, args);
        logger.error("代理启动成功");
    }

}
