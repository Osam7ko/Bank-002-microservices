package com.osama.bank002.profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.osama.bank002.profile.client")
@SpringBootApplication
public class ProfileServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfileServicesApplication.class, args);
    }

}