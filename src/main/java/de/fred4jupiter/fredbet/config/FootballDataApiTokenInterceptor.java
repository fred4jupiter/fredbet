package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.integration.FootballDataService;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FootballDataApiTokenInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataApiTokenInterceptor.class);

    private static final String FOOTBALL_DATA_API_TOKEN_HTTP_HEADER_NAME = "X-Auth-Token";

    private final FootballDataService footballDataService;

    private final FredbetProperties fredbetProperties;

    public FootballDataApiTokenInterceptor(FootballDataService footballDataService, FredbetProperties fredbetProperties) {
        this.footballDataService = footballDataService;
        this.fredbetProperties = fredbetProperties;
    }

    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte @NonNull [] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        final String apiToken = fetchApiToken();
        if (StringUtils.isNotBlank(apiToken)) {
            request.getHeaders().add(FOOTBALL_DATA_API_TOKEN_HTTP_HEADER_NAME, apiToken);
            return execution.execute(request, body);
        }
        return execution.execute(request, body);
    }

    private String fetchApiToken() {
        final String apiToken = footballDataService.loadSettings().getApiToken();
        if (StringUtils.isNotBlank(apiToken)) {
            return apiToken;
        }

        // look for fallback API token in config
        String apiTokenFromConfig = this.fredbetProperties.integration().footballData().apiToken();
        if (StringUtils.isNotBlank(apiTokenFromConfig)) {
            LOG.info("Using API token from config...");
            return apiTokenFromConfig;
        }

        throw new FootballDataApiTokenNotConfiguredException("There is no football-data.org authentication token configured!");
    }
}
