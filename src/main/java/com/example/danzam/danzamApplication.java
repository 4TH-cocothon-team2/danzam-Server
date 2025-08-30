package com.example.danzam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.example.danzam", "com.app"})
@EnableJpaRepositories(basePackages = {"com.app"})
@EntityScan(basePackages = {"com.app"})
public class danzamApplication {
	public static void main(String[] args) {
		SpringApplication.run(danzamApplication.class, args);
	}

}
