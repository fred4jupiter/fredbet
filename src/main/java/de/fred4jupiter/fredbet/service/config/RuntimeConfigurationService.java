package de.fred4jupiter.fredbet.service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.repository.RuntimeConfigRepository;

/**
 * Service for loading runtime configuration settings.
 * 
 * @author michael
 *
 */
@Service
@Transactional
public class RuntimeConfigurationService {

	private static final Logger LOG = LoggerFactory.getLogger(RuntimeConfigurationService.class);

	@Autowired
	private RuntimeConfigRepository runtimeConfigRepository;

	@Cacheable(CacheNames.RUNTIME_CONFIG)
	public RuntimeConfig loadRuntimeConfig() {
		LOG.debug("Loading runtime configuration from DB...");
		return runtimeConfigRepository.loadRuntimeConfig();
	}

	@CacheEvict(cacheNames = CacheNames.RUNTIME_CONFIG, allEntries = true)
	public void saveRuntimeConfig(RuntimeConfig runtimeConfig) {
		runtimeConfigRepository.saveRuntimeConfig(runtimeConfig);
	}

}
