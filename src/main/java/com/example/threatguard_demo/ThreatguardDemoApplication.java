package com.example.threatguard_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ThreatguardDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThreatguardDemoApplication.class, args);
	}

}
