package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.integration.FootballDataClient;
import de.fred4jupiter.fredbet.props.FootballDataProperties;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class FootballDataConfig {

    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory(FredbetProperties fredbetProperties, FootballDataApiTokenInterceptor interceptor) {
        final FootballDataProperties footballDataProperties = fredbetProperties.integration().footballData();
        final RestClient restClient = RestClient.builder().baseUrl(footballDataProperties.baseUrl()).requestInterceptor(interceptor).build();

        final RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(restClientAdapter).build();
    }

    @Bean
    public FootballDataClient footballDataClient(HttpServiceProxyFactory factory) {
        return factory.createClient(FootballDataClient.class);
    }
}
