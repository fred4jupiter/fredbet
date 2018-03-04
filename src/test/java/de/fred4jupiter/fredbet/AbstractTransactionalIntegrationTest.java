package de.fred4jupiter.fredbet;

import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Rollback(true)
@Transactional
public abstract class AbstractTransactionalIntegrationTest extends AbstractIntegrationTest {

}
