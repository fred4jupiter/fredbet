package de.fred4jupiter.fredbet.service.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.domain.RuntimeConfigDb;
import de.fred4jupiter.fredbet.repository.RuntimeConfigDbRepository;

@Service
@Transactional
public class RuntimeConfigurationService {

	private static final Long DEFAULT_CONFIG_ID = Long.valueOf(1);

	@Autowired
	private RuntimeConfigDbRepository runtimeConfigRepository;

	public RuntimeConfig loadRuntimeConfig() {
		RuntimeConfigDb runtimeConfigDb = loadRuntimeConfigDb();
		String jsonConfig = runtimeConfigDb.getJsonConfig();

		if (StringUtils.isNotBlank(jsonConfig)) {
			Gson gson = new Gson();
			return gson.fromJson(jsonConfig, RuntimeConfig.class);
		}
		else {
			return new RuntimeConfig();
		}
	}

	private RuntimeConfigDb loadRuntimeConfigDb() {
		RuntimeConfigDb runtimeConfigDb = runtimeConfigRepository.findOne(DEFAULT_CONFIG_ID);
		if (runtimeConfigDb == null) {
			runtimeConfigDb = new RuntimeConfigDb(DEFAULT_CONFIG_ID);
			runtimeConfigRepository.save(runtimeConfigDb);
		}

		return runtimeConfigDb;
	}

	public void saveRuntimeConfig(RuntimeConfig runtimeConfig) {
		RuntimeConfigDb runtimeConfigDb = loadRuntimeConfigDb();

		Gson gson = new Gson();
		String json = gson.toJson(runtimeConfig);

		runtimeConfigDb.setJsonConfig(json);

		runtimeConfigRepository.save(runtimeConfigDb);
	}
}
