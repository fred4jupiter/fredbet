package de.fred4jupiter.fredbet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractTransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.RuntimeConfigDb;

public class RuntimeConfigDbRepositoryIT extends AbstractTransactionalIntegrationTest {

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
