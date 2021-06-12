package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.RuntimeSettingsDb;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RuntimeSettingsRepository {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeSettingsRepository.class);

    private final RuntimeSettingsDbRepository runtimeSettingsDbRepository;

    private final JsonObjectConverter jsonObjectConverter;

    public RuntimeSettingsRepository(RuntimeSettingsDbRepository runtimeSettingsDbRepository, JsonObjectConverter jsonObjectConverter) {
        this.runtimeSettingsDbRepository = runtimeSettingsDbRepository;
        this.jsonObjectConverter = jsonObjectConverter;
    }

    public <T> T loadRuntimeSettings(Long id, Class<T> targetType) {
        RuntimeSettingsDb runtimeSettingsDb = loadRuntimeSettingsDb(id);
        return toRuntimeSettings(runtimeSettingsDb, targetType);
    }

    private RuntimeSettingsDb loadRuntimeSettingsDb(Long id) {
        Optional<RuntimeSettingsDb> runtimeSettingsDbOpt = runtimeSettingsDbRepository.findById(id);
        if (runtimeSettingsDbOpt.isPresent()) {
            return runtimeSettingsDbOpt.get();
        }

        RuntimeSettingsDb runtimeSettingsDb = new RuntimeSettingsDb(id);
        runtimeSettingsDbRepository.save(runtimeSettingsDb);
        return runtimeSettingsDb;
    }

    private <T> T toRuntimeSettings(RuntimeSettingsDb runtimeSettingsDb, Class<T> targetType) {
        String jsonConfig = runtimeSettingsDb.getJsonConfig();

        if (StringUtils.isNotBlank(jsonConfig)) {
            return jsonObjectConverter.fromJson(jsonConfig, targetType);
        }

        return null;
    }

    public <T> void saveRuntimeSettings(Long id, T runtimeSettings) {
        RuntimeSettingsDb runtimeSettingsDb = toRuntimeSettingsDb(id, runtimeSettings);

        runtimeSettingsDbRepository.save(runtimeSettingsDb);
        LOG.info("saved runtime settings.");
    }

    private <T> RuntimeSettingsDb toRuntimeSettingsDb(Long id, T runtimeSettings) {
        String json = jsonObjectConverter.toJson(runtimeSettings);
        RuntimeSettingsDb runtimeSettingsDb = loadRuntimeSettingsDb(id);
        runtimeSettingsDb.setJsonConfig(json);
        return runtimeSettingsDb;
    }

}
