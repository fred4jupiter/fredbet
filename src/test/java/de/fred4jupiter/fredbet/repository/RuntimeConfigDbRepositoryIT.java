package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.RuntimeConfigDb;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class RuntimeConfigDbRepositoryIT {

    @Autowired
    private RuntimeConfigDbRepository runtimeConfigRepository;

    @Test
    public void saveAndRead() {
        long configId = 1L;
        RuntimeConfigDb runtimeConfigDb = new RuntimeConfigDb(configId);
        runtimeConfigDb.setJsonConfig("{bla:1}");

        runtimeConfigRepository.save(runtimeConfigDb);

        RuntimeConfigDb found = runtimeConfigRepository.getOne(configId);
        assertNotNull(found);

        assertEquals(runtimeConfigDb.getConfigId(), found.getConfigId());
    }
}
