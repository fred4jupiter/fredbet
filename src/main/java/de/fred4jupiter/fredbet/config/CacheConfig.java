package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.props.CacheNames;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<>();
        caches.add(new ConcurrentMapCache(CacheNames.AVAIL_GROUPS));
        caches.add(new ConcurrentMapCache(CacheNames.CHILD_RELATION));
        caches.add(new ConcurrentMapCache(CacheNames.RUNTIME_SETTINGS));
        caches.add(new ConcurrentMapCache(CacheNames.POINTS_CONFIG));
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
