package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.props.FootballDataProperties;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class FootballDataConfig {

    @Bean
    public RestClient restClient(FredbetProperties fredbetProperties, FootballDataApiTokenInterceptor interceptor) {
        final FootballDataProperties footballDataProperties = fredbetProperties.integration().footballData();
        return RestClient.builder().baseUrl(footballDataProperties.baseUrl()).requestInterceptor(interceptor).build();
    }
}
