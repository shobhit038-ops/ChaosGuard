package com.example.ChasosGuard;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@OpenAPIDefinition(
		info = @Info(
				title = "Chaos Guard REST API Documentation",
				description = "Chaos Guard REST API Documentation",
				version = "v1"
		)
)
@EnableScheduling
@SpringBootApplication
public class ChasosGuardApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChasosGuardApplication.class, args);
	}
}
