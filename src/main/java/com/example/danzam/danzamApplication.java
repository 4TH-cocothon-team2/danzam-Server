package com.example.danzam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.example.danzam", "com.app.analysis"})
@EnableJpaRepositories(basePackages = {"com.app.analysis"})
@EntityScan(basePackages = {"com.app.analysis"})
public class danzamApplication {
	public static void main(String[] args) {
		SpringApplication.run(danzamApplication.class, args);
	}

}
