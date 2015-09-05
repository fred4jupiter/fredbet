package de.fred4jupiter.fredbet;

import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

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
    
    @Bean
    public Properties gitProperties() throws IOException {
    	return PropertiesLoaderUtils.loadProperties(new ClassPathResource("git.properties"));
    }
}
