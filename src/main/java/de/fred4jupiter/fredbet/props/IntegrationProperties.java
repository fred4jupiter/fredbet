package de.fred4jupiter.fredbet.props;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public record IntegrationProperties(@NestedConfigurationProperty FootballDataProperties footballData) {

}
