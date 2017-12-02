package de.fred4jupiter.fredbet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.aws.autoconfigure.cache.ElastiCacheAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.DatabaseType;
import de.fred4jupiter.fredbet.props.FredbetProperties;

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
@EnableCaching
@EnableAutoConfiguration(exclude = ElastiCacheAutoConfiguration.class)
public class Application {

	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

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
		LOG.info("fredbetProperties: {}", fredbetProperties);
		final HikariConfig config = new HikariConfig();
		config.setPoolName("FredBetCP");
		config.setConnectionTestQuery("SELECT 1");
		config.setJdbcUrl(fredbetProperties.getDatabaseUrl());
		config.setUsername(fredbetProperties.getDatabaseUsername());
		config.setPassword(fredbetProperties.getDatabasePassword());
		config.setMaximumPoolSize(20);
		config.setIdleTimeout(30000);

		final DatabaseType databaseType = fredbetProperties.getDatabaseType();
		config.setDriverClassName(databaseType.getDriverClassName());
		config.addDataSourceProperty("hibernate.dialect", databaseType.getDatabasePlatform());

		return new HikariDataSource(config);
	}

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		List<Cache> caches = new ArrayList<Cache>();
		caches.add(new ConcurrentMapCache(CacheNames.AVAIL_GROUPS));
		caches.add(new ConcurrentMapCache(CacheNames.CHILD_RELATION));
		caches.add(new ConcurrentMapCache(CacheNames.RUNTIME_CONFIG));
		cacheManager.setCaches(caches);
		return cacheManager;
	}
}
