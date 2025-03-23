package de.fred4jupiter.fredbet.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Contains all configuration properties for FredBet application.
 *
 * @author michael
 */
@ConfigurationProperties(prefix = "fredbet")
public record FredbetProperties(ImageLocation imageLocation, String imageFileSystemBaseFolder, String defaultLanguage,
                                Integer diceMinRange, Integer diceMaxRange, Integer thumbnailSize,
                                String adminUsername, String adminPassword,
                                @NestedConfigurationProperty AuthorizationProperties authorization,
                                @NestedConfigurationProperty DefaultImageProperties defaultImages) {

}
