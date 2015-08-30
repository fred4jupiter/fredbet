package de.fred4jupiter.fredbet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application start.
 * 
 * NOTE: To disable thymeleaf caching correctly please activate the dev profile on local development.
 * 
 * @author michael
 *
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	SpringApplication app = new SpringApplication(Application.class);
    	app.addInitializers(new AppInitializer());
    	app.run(args);
    }
}
