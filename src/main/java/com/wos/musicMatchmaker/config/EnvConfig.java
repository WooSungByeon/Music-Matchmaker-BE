package com.wos.musicMatchmaker.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

	@Value("${env.dir}")
	private String ENV_FILE_DIR;

	@PostConstruct
	public void loadDotenv() {
		Dotenv dotenv = Dotenv.configure()
								.directory(ENV_FILE_DIR)
								.load();

		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
	}
}
