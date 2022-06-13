package com.fflog.fflogbot;

import com.fflog.fflogbot.listeners.CharacterListener;
import com.fflog.fflogbot.client.ClientWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class FflogbotApplication {

	@Autowired
	private Environment env;

	@Autowired
	private ClientWrapper clientWrapper;

	@Autowired
	private CharacterListener listener;


	public static void main(String[] args) {
		SpringApplication.run(FflogbotApplication.class, args);
	}

	@Bean
	@ConfigurationProperties
	public JDA discordApi() throws LoginException, IOException, ExecutionException, InterruptedException {
		String token = env.getProperty("TOKEN");

		JDA api = JDABuilder.createDefault(token).build();
		api.addEventListener(listener);

		return api;
	}
}
