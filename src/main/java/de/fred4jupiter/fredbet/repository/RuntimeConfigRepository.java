package de.fred4jupiter.fredbet.repository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import de.fred4jupiter.fredbet.domain.RuntimeConfigDb;

@Repository
public class RuntimeConfigRepository {

	private static final Logger LOG = LoggerFactory.getLogger(RuntimeConfigRepository.class);

	@Autowired
	private RuntimeConfigDbRepository runtimeConfigDbRepository;

	public <T> T loadRuntimeConfig(Long id, Class<T> targetType) {
		RuntimeConfigDb runtimeConfigDb = runtimeConfigDbRepository.findOne(id);
		if (runtimeConfigDb == null) {
			runtimeConfigDb = new RuntimeConfigDb(id);
			runtimeConfigDbRepository.save(runtimeConfigDb);
		}

		return toRuntimeConfig(runtimeConfigDb, targetType);
	}

	private <T> T toRuntimeConfig(RuntimeConfigDb runtimeConfigDb, Class<T> targetType) {
		String jsonConfig = runtimeConfigDb.getJsonConfig();

		if (StringUtils.isNotBlank(jsonConfig)) {
			Gson gson = new Gson();
			return gson.fromJson(jsonConfig, targetType);
		}

		return null;
	}

	public <T> void saveRuntimeConfig(Long id, T runtimeConfig) {
		RuntimeConfigDb runtimeConfigDb = toRuntimeConfigDb(id, runtimeConfig);

		runtimeConfigDbRepository.save(runtimeConfigDb);
		LOG.info("saved runtime configuration.");
	}

	private <T> RuntimeConfigDb toRuntimeConfigDb(Long id, T runtimeConfig) {
		Gson gson = new Gson();
		String json = gson.toJson(runtimeConfig);
		RuntimeConfigDb runtimeConfigDb = loadRuntimeConfigDb(id);
		runtimeConfigDb.setJsonConfig(json);
		return runtimeConfigDb;
	}

	private RuntimeConfigDb loadRuntimeConfigDb(Long id) {
		RuntimeConfigDb runtimeConfigDb = runtimeConfigDbRepository.findOne(id);
		if (runtimeConfigDb == null) {
			runtimeConfigDb = new RuntimeConfigDb(id);
			runtimeConfigDbRepository.save(runtimeConfigDb);
		}

		return runtimeConfigDb;
	}

}
