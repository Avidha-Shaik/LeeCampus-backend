package com.leecampus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication
@EnableScheduling

public class LeetCampusApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeetCampusApplication.class, args);
	}

}
