package com.nourishcare.visionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class VisionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VisionServiceApplication.class, args);
    }
}
