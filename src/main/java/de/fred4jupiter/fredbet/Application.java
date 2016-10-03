package de.fred4jupiter.fredbet;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Main application start.
 * 
 * NOTE: To disable thymeleaf caching correctly please activate the dev profile
 * on local development.
 * 
 * @author michael
 *
 */
@SpringBootApplication
@EnableConfigurationProperties(FredbetProperties.class)
public class Application {

	@Autowired
	private Environment environment;

	static {
		// to avoid warning 'Unable to instantiate
		// org.fusesource.jansi.WindowsAnsiOutputStream'
		System.setProperty("log4j.skipJansi", "true");
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.addInitializers(new AppInitializer());
		app.run(args);
	}

	@Bean
	public Properties buildProperties() throws IOException {
		return PropertiesLoaderUtils.loadProperties(new ClassPathResource("build.properties"));
	}

	@Bean(destroyMethod = "close")
	public DataSource dataSource(FredbetProperties fredbetProperties) {
		HikariConfig config = new HikariConfig();
		config.setPoolName("FredBetCP");
		config.setConnectionTestQuery("SELECT 1");
		config.setJdbcUrl(fredbetProperties.getDatabaseUrl());
		config.setUsername(fredbetProperties.getDatabaseUsername());
		config.setPassword(fredbetProperties.getDatabasePassword());
		config.setMaximumPoolSize(20);
		config.setIdleTimeout(30000);

		if (environment.acceptsProfiles("dev", "default")) {
			config.setDriverClassName(org.h2.Driver.class.getName());
		} else {
			config.setDriverClassName(org.mariadb.jdbc.Driver.class.getName());
		}

		return new HikariDataSource(config);
	}
}
