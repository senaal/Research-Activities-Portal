package com.uni.research_portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ResearchPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResearchPortalApplication.class, args);
	}

}
