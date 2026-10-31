package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.props.FredBetProfile;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the Liquibase changelog and the Hibernate schema validation against a real PostgreSQL database.
 * Skipped if no Docker is available.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles(FredBetProfile.INTEGRATION_TEST)
public class PostgreSqlDatabaseContentIT extends AbstractDatabaseContentIT {

    // the same image as in the docker-compose.yml
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.4-alpine");

    @AfterEach
    public void noLargeObjects() {
        // large objects would not be removed together with their row
        assertThat(jdbcTemplate.queryForObject("select count(*) from pg_largeobject_metadata", Long.class)).isZero();
    }
}
