package com.project.emprendia.entrepreneurship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EntrepreneurshipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EntrepreneurshipServiceApplication.class, args);
    }
}
