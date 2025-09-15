package com.nourishcare.mealplanningservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MealPlanningServiceApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .ignoreIfMissing()
                .load();
        
        // Set system properties for Spring to pick up
        System.setProperty("MONGODB_URI", dotenv.get("MONGODB_URI", "mongodb://localhost:27017/NourishCare"));
        
        SpringApplication.run(MealPlanningServiceApplication.class, args);
    }
}
