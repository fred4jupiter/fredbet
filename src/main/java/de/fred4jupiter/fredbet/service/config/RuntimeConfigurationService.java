package de.fred4jupiter.fredbet.service.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.domain.RuntimeConfigDb;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.repository.RuntimeConfigDbRepository;

@Service
@Transactional
public class RuntimeConfigurationService {

	private static final Logger LOG = LoggerFactory.getLogger(RuntimeConfigurationService.class);

	private static final Long DEFAULT_CONFIG_ID = Long.valueOf(1);

	@Autowired
	private RuntimeConfigDbRepository runtimeConfigRepository;

	@Autowired
	private Environment environment;

	@Cacheable(CacheNames.RUNTIME_CONFIG)
	public RuntimeConfig loadRuntimeConfig() {
		LOG.debug("Loading runtime configuration from DB...");
		RuntimeConfigDb runtimeConfigDb = loadRuntimeConfigDb();
		String jsonConfig = runtimeConfigDb.getJsonConfig();

		if (StringUtils.isNotBlank(jsonConfig)) {
			Gson gson = new Gson();
			return gson.fromJson(jsonConfig, RuntimeConfig.class);
		} else {
			return createDefaultRuntimeConfig();
		}
	}

	private RuntimeConfig createDefaultRuntimeConfig() {
		RuntimeConfig runtimeConfig = new RuntimeConfig();
		if (environment.acceptsProfiles(FredBetProfile.DEV)) {
			runtimeConfig.setShowDemoDataNavigationEntry(true);
			runtimeConfig.setEnableChangingUsername(true);
			runtimeConfig.setCreateDemoData(true);
			runtimeConfig.setFavouriteCountry(Country.GERMANY);
			saveRuntimeConfig(runtimeConfig);
		}

		return runtimeConfig;
	}

	@CacheEvict(cacheNames = CacheNames.RUNTIME_CONFIG, allEntries = true)
	public void saveRuntimeConfig(RuntimeConfig runtimeConfig) {
		RuntimeConfigDb runtimeConfigDb = loadRuntimeConfigDb();

		Gson gson = new Gson();
		String json = gson.toJson(runtimeConfig);

		runtimeConfigDb.setJsonConfig(json);

		runtimeConfigRepository.save(runtimeConfigDb);
		LOG.info("saved runtime configuration.");
	}

	private RuntimeConfigDb loadRuntimeConfigDb() {
		RuntimeConfigDb runtimeConfigDb = runtimeConfigRepository.findOne(DEFAULT_CONFIG_ID);
		if (runtimeConfigDb == null) {
			runtimeConfigDb = new RuntimeConfigDb(DEFAULT_CONFIG_ID);
			runtimeConfigRepository.save(runtimeConfigDb);
		}

		return runtimeConfigDb;
	}
}
