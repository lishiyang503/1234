package org.example.springboot_vaue_demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.demo")
@MapperScan("com.demo.mapper")
public class SpringbootVaueDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootVaueDemoApplication.class, args);
    }
}