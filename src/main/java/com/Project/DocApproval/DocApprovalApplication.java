package com.Project.DocApproval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableAsync // This is mandatory for background processing
public class DocApprovalApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocApprovalApplication.class, args);
	}

}
