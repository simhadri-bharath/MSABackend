package com.bharath.msa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {        
		if (!isTestEnvironment()) { // Skip .env loading in test environments
	        Dotenv dotenv = Dotenv.configure()
	            .filename(".env")
	            .load();
	        dotenv.entries().forEach(entry ->
	            System.setProperty(entry.getKey(), entry.getValue())
	        );
	    }

		
		
		SpringApplication.run(DemoApplication.class, args);
	}
	private static boolean isTestEnvironment() {
	    return System.getProperty("spring.test.context") != null;
	}
	 @Bean
	    public WebMvcConfigurer corsConfigurer() {
	        // Fetch the allowed origins from the environment variable
	        String allowedOriginsEnv = System.getenv("ALLOWED_ORIGINS");
	        String[] allowedOrigins = allowedOriginsEnv != null ? allowedOriginsEnv.split(",") : new String[]{"http://localhost:3000"};

	        return new WebMvcConfigurer() {
	            @Override
	            public void addCorsMappings(CorsRegistry registry) {
	                registry.addMapping("/**") // Apply to all endpoints
	                        .allowedOrigins(allowedOrigins) // Allow origins from the .env file
	                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specific HTTP methods
	                        .allowedHeaders("*") // Allow all headers
	                        .allowCredentials(true); // Allow credentials (cookies, etc.)
	            }
	        };
	    }
	
   
}

