package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.security.FredBetUserGroups;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * Main application start.
 * <p>
 * NOTE: To disable thymeleaf caching correctly please activate the dev profile
 * on local development.
 *
 * @author michael
 */
@SpringBootApplication
@EnableConfigurationProperties({FredbetProperties.class, FredBetUserGroups.class})
@EnableCaching
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<>();
        caches.add(new ConcurrentMapCache(CacheNames.AVAIL_GROUPS));
        caches.add(new ConcurrentMapCache(CacheNames.CHILD_RELATION));
        caches.add(new ConcurrentMapCache(CacheNames.RUNTIME_SETTINGS));
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
