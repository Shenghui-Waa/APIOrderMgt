package com.apimgt.apiordermgt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.apimgt.apiordermgt.mapper")
public class ApiOrderMgtApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiOrderMgtApplication.class, args);
    }

}
