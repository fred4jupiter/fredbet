package de.fred4jupiter.fredbet.service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.props.FredbetConstants;
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

	private static final Long DEFAULT_CONFIG_ID = Long.valueOf(1);

	@Autowired
	private RuntimeConfigRepository runtimeConfigRepository;

	@Autowired
	private Environment environment;

	@Cacheable(CacheNames.RUNTIME_CONFIG)
	public RuntimeConfig loadRuntimeConfig() {
		LOG.debug("Loading runtime configuration from DB...");
		RuntimeConfig runtimeConfig = runtimeConfigRepository.loadRuntimeConfig(DEFAULT_CONFIG_ID, RuntimeConfig.class);
		if (runtimeConfig == null) {
			runtimeConfig = createDefaultRuntimeConfig();
		}

		return runtimeConfig;
	}

	private RuntimeConfig createDefaultRuntimeConfig() {
		RuntimeConfig runtimeConfig = new RuntimeConfig();
		runtimeConfig.setFavouriteCountry(FredbetConstants.DEFAULT_FAVOURITE_COUNTRY);
		runtimeConfig.setPasswordForReset(FredbetConstants.DEFAULT_REST_PASSWORT);

		if (environment.acceptsProfiles(FredBetProfile.DEV)) {
			runtimeConfig.setShowDemoDataNavigationEntry(true);
			runtimeConfig.setEnableChangingUsername(true);
			runtimeConfig.setCreateDemoData(true);
			runtimeConfig.setEnabledParentChildRanking(true);

			saveRuntimeConfig(runtimeConfig);
		}

		return runtimeConfig;
	}

	@CacheEvict(cacheNames = CacheNames.RUNTIME_CONFIG, allEntries = true)
	public void saveRuntimeConfig(RuntimeConfig runtimeConfig) {
		runtimeConfigRepository.saveRuntimeConfig(DEFAULT_CONFIG_ID, runtimeConfig);
	}

}
