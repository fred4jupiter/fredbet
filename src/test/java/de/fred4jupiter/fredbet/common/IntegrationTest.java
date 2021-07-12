package de.fred4jupiter.fredbet.common;

import de.fred4jupiter.fredbet.Application;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Composed annotation for integration tests.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles(value = {FredBetProfile.INTEGRATION_TEST})
public @interface IntegrationTest {
}
