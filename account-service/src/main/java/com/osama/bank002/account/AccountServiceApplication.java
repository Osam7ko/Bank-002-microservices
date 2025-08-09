package com.osama.bank002.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.osama.bank002.account.client")
@SpringBootApplication
public class AccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

}