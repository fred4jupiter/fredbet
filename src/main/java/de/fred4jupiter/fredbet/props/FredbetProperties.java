package de.fred4jupiter.fredbet.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/**
 * Contains all configuration properties for FredBet application.
 * <p>
 * {@code rememberMeValidity}: how long the auto-login (remember-me) keeps a user logged in after the last visit,
 * e.g. {@code 30d} or {@code 12h}. Every automatic login starts this period again.
 *
 * @author michael
 */
@ConfigurationProperties(prefix = "fredbet")
public record FredbetProperties(String defaultLanguage,
                                Integer diceMinRange, Integer diceMaxRange, Integer thumbnailSize,
                                String adminUsername, String adminPassword,
                                @DefaultValue("30d") Duration rememberMeValidity,
                                @NestedConfigurationProperty AuthorizationProperties authorization,
                                @NestedConfigurationProperty IntegrationProperties integration) {

}
