package com.nourishcare.recipeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RecipeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeServiceApplication.class, args);
    }
}
