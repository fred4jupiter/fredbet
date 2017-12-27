package de.fred4jupiter.fredbet.service.admin;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for clearing all caches.
 * 
 * <p>See Application for configuring the caches.
 * 
 * @author michael
 *
 */
@Service
public class CacheAdministrationService {

	private static final Logger LOG = LoggerFactory.getLogger(CacheAdministrationService.class);

	@Autowired
	private CacheManager cacheManager;

	public void clearCaches() {
		Collection<String> cacheNames = this.cacheManager.getCacheNames();
		if (CollectionUtils.isEmpty(cacheNames)) {
			LOG.info("No caches available. Nothing to clear.");
			return;
		}
		
		for (String cacheName : cacheNames) {
			Cache cache = this.cacheManager.getCache(cacheName);
			if (cache != null) {
				LOG.info("Clearing cache: {}", cache.getName());
				cache.clear();
			}
		}
	}
}
