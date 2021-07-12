package de.fred4jupiter.fredbet.common;

import de.fred4jupiter.fredbet.props.FredBetProfile;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Only useful for database integration tests not starting the complete application context.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@ActiveProfiles({FredBetProfile.INTEGRATION_TEST})
public @interface DatabaseIntegrationTest {
}
