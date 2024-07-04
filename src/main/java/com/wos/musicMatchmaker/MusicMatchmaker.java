package com.wos.musicMatchmaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MusicMatchmaker {

	public static void main(String[] args) {
		SpringApplication.run(MusicMatchmaker.class, args);
	}

}
