package com.rhcloud.tutorials.spring_oauth2_discord;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class App {
	/**
	 * RESTful request handler for HTTP.
	 * 
	 * @return the list of command prefixes.
	 */
	@RequestMapping("/")
	public String home() {
		return "wololo";
	}

	/**
	 * Main method run statically.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// get token as environment variable.
		// final String token = System.getenv("gow_token");

		SpringApplication.run(App.class, args);
	}

	/**
	 * Bean that lists java Beans registered by Spring.
	 * 
	 * @param ctx
	 * @return
	 */
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println();
		};
	}

}
