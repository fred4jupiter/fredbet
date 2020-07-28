package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.RuntimeConfigDb;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RuntimeConfigRepository<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeConfigRepository.class);

    private final RuntimeConfigDbRepository runtimeConfigDbRepository;

    private final JsonObjectConverter jsonObjectConverter;

    public RuntimeConfigRepository(RuntimeConfigDbRepository runtimeConfigDbRepository, JsonObjectConverter jsonObjectConverter) {
        this.runtimeConfigDbRepository = runtimeConfigDbRepository;
        this.jsonObjectConverter = jsonObjectConverter;
    }

    public T loadRuntimeConfig(Long id, Class<T> targetType) {
        RuntimeConfigDb runtimeConfigDb = loadRuntimeConfigDb(id);
        return toRuntimeConfig(runtimeConfigDb, targetType);
    }

    private RuntimeConfigDb loadRuntimeConfigDb(Long id) {
        Optional<RuntimeConfigDb> runtimeConfigDbOpt = runtimeConfigDbRepository.findById(id);
        if (runtimeConfigDbOpt.isPresent()) {
            return runtimeConfigDbOpt.get();
        }

        RuntimeConfigDb runtimeConfigDb = new RuntimeConfigDb(id);
        runtimeConfigDbRepository.save(runtimeConfigDb);
        return runtimeConfigDb;
    }

    private T toRuntimeConfig(RuntimeConfigDb runtimeConfigDb, Class<T> targetType) {
        String jsonConfig = runtimeConfigDb.getJsonConfig();

        if (StringUtils.isNotBlank(jsonConfig)) {
            return jsonObjectConverter.fromJson(jsonConfig, targetType);
        }

        return null;
    }

    public void saveRuntimeConfig(Long id, T runtimeConfig) {
        RuntimeConfigDb runtimeConfigDb = toRuntimeConfigDb(id, runtimeConfig);

        runtimeConfigDbRepository.save(runtimeConfigDb);
        LOG.info("saved runtime configuration.");
    }

    private RuntimeConfigDb toRuntimeConfigDb(Long id, T runtimeConfig) {
        String json = jsonObjectConverter.toJson(runtimeConfig);
        RuntimeConfigDb runtimeConfigDb = loadRuntimeConfigDb(id);
        runtimeConfigDb.setJsonConfig(json);
        return runtimeConfigDb;
    }

}
