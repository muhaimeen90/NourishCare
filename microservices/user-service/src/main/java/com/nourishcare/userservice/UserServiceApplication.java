package com.nourishcare.userservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .ignoreIfMissing()
                .load();
        
        // Set system properties for Spring to pick up
        System.setProperty("MONGODB_URI", dotenv.get("MONGODB_URI", "mongodb://localhost:27017/NourishCare"));
        
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
