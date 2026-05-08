package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.TeamService;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MatchBuilder matchBuilder(TeamService teamService) {
        return MatchBuilder.create(teamService);
    }
}
