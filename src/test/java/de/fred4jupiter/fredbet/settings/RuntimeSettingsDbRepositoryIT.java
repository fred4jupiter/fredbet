package de.fred4jupiter.fredbet.settings;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class RuntimeSettingsDbRepositoryIT {

    @Autowired
    private RuntimeSettingsDbRepository runtimeSettingsDbRepository;

    @Test
    public void saveAndRead() {
        long configId = 1L;
        RuntimeSettingsDb runtimeSettingsDb = new RuntimeSettingsDb(configId);
        runtimeSettingsDb.setJsonConfig("{bla:1}");

        runtimeSettingsDbRepository.save(runtimeSettingsDb);

        RuntimeSettingsDb found = runtimeSettingsDbRepository.getReferenceById(configId);
        assertNotNull(found);

        assertEquals(runtimeSettingsDb.getConfigId(), found.getConfigId());
    }
}
