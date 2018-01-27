package de.fred4jupiter.fredbet.repository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.domain.RuntimeConfigDb;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.props.FredbetConstants;

@Repository
public class RuntimeConfigRepository {

	private static final Logger LOG = LoggerFactory.getLogger(RuntimeConfigRepository.class);

	private static final Long DEFAULT_CONFIG_ID = Long.valueOf(1);

	@Autowired
	private RuntimeConfigDbRepository runtimeConfigDbRepository;

	@Autowired
	private Environment environment;

	public RuntimeConfig loadRuntimeConfig() {
		RuntimeConfigDb runtimeConfigDb = runtimeConfigDbRepository.findOne(DEFAULT_CONFIG_ID);
		if (runtimeConfigDb == null) {
			runtimeConfigDb = new RuntimeConfigDb(DEFAULT_CONFIG_ID);
			runtimeConfigDbRepository.save(runtimeConfigDb);
		}

		return toRuntimeConfig(runtimeConfigDb);
	}

	private RuntimeConfig toRuntimeConfig(RuntimeConfigDb runtimeConfigDb) {
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

	public void saveRuntimeConfig(RuntimeConfig runtimeConfig) {
		RuntimeConfig runtimeConfigFromDb = toRuntimeConfig(loadRuntimeConfigDb());
		updatePropertiesFromTo(runtimeConfig, runtimeConfigFromDb);

		RuntimeConfigDb runtimeConfigDb = toRuntimeConfigDb(runtimeConfigFromDb);

		runtimeConfigDbRepository.save(runtimeConfigDb);
		LOG.info("saved runtime configuration.");
	}

	private void updatePropertiesFromTo(RuntimeConfig from, RuntimeConfig to) {
		BeanUtils.copyProperties(from, to);
	}

	private RuntimeConfigDb toRuntimeConfigDb(RuntimeConfig runtimeConfig) {
		Gson gson = new Gson();
		String json = gson.toJson(runtimeConfig);
		RuntimeConfigDb runtimeConfigDb = loadRuntimeConfigDb();
		runtimeConfigDb.setJsonConfig(json);
		return runtimeConfigDb;
	}

	private RuntimeConfigDb loadRuntimeConfigDb() {
		RuntimeConfigDb runtimeConfigDb = runtimeConfigDbRepository.findOne(DEFAULT_CONFIG_ID);
		if (runtimeConfigDb == null) {
			runtimeConfigDb = new RuntimeConfigDb(DEFAULT_CONFIG_ID);
			runtimeConfigDbRepository.save(runtimeConfigDb);
		}

		return runtimeConfigDb;
	}

}
