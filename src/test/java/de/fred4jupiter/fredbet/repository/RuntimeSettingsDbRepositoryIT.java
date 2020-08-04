package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.RuntimeSettingsDb;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class RuntimeSettingsDbRepositoryIT {

    @Autowired
    private RuntimeSettingsDbRepository runtimeConfigRepository;

    @Test
    public void saveAndRead() {
        long configId = 1L;
        RuntimeSettingsDb runtimeSettingsDb = new RuntimeSettingsDb(configId);
        runtimeSettingsDb.setJsonConfig("{bla:1}");

        runtimeConfigRepository.save(runtimeSettingsDb);

        RuntimeSettingsDb found = runtimeConfigRepository.getOne(configId);
        assertNotNull(found);

        assertEquals(runtimeSettingsDb.getConfigId(), found.getConfigId());
    }
}
