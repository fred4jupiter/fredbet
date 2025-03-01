package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application start.
 * <p>
 * NOTE: To disable thymeleaf caching correctly please activate the dev profile
 * on local development.
 *
 * @author michael
 */
@SpringBootApplication
@EnableConfigurationProperties(FredbetProperties.class)
@EnableCaching
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }
}
