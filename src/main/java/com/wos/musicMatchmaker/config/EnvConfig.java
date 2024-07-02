package com.wos.musicMatchmaker.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

	@PostConstruct
	public void loadDotenv() {
		Dotenv dotenv = Dotenv.configure()
//								.directory("YOUR .env file dir")
								.load();

		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
	}
}
