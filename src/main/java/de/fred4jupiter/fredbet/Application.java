package de.fred4jupiter.fredbet;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
public class Application {

	@Autowired
	private Environment environment;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.addInitializers(new AppInitializer());
		app.run(args);
	}

	@Bean
	public Properties buildProperties() throws IOException {
		return PropertiesLoaderUtils.loadProperties(new ClassPathResource("build.properties"));
	}

	@Bean
	public HikariConfig hikariConfig() {
		HikariConfig config = new HikariConfig();
		config.setPoolName("FredBet");
		config.setConnectionTestQuery("SELECT 1 FROM DUAL");
		config.setDriverClassName(environment.getProperty("spring.datasource.driverclassName"));
		config.setJdbcUrl(environment.getProperty("spring.datasource.url"));
		config.setUsername(environment.getProperty("spring.datasource.username"));
		config.setPassword(environment.getProperty("spring.datasource.password"));
		config.setMaximumPoolSize(10);
		config.setIdleTimeout(30000);
		return config;
	}

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		return new HikariDataSource(hikariConfig());
	}
}
