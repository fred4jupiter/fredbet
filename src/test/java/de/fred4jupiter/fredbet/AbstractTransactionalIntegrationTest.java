package de.fred4jupiter.fredbet;

import javax.transaction.Transactional;

import org.springframework.test.annotation.Rollback;

@Rollback(true)
@Transactional
public abstract class AbstractTransactionalIntegrationTest extends AbstractIntegrationTest {

}
