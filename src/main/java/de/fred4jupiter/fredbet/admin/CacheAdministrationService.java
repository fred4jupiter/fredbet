package de.fred4jupiter.fredbet.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * Service for clearing all caches.
 *
 * <p>See Application for configuring the caches.
 *
 * @author michael
 */
@Service
public class CacheAdministrationService {

    private static final Logger LOG = LoggerFactory.getLogger(CacheAdministrationService.class);

    private final CacheManager cacheManager;

    public CacheAdministrationService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearCaches() {
        Collection<String> cacheNames = this.cacheManager.getCacheNames();
        if (CollectionUtils.isEmpty(cacheNames)) {
            LOG.info("No caches available. Nothing to clear.");
            return;
        }

        cacheNames.forEach(this::clearCacheByCacheName);
    }

    public void clearCacheByCacheName(String cacheName) {
        Cache cache = this.cacheManager.getCache(cacheName);
        if (cache != null) {
            LOG.info("Clearing cache: {}", cache.getName());
            cache.clear();
        }
    }
}
