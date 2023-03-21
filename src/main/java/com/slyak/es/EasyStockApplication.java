package com.slyak.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EasyStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyStockApplication.class, args);
    }

}