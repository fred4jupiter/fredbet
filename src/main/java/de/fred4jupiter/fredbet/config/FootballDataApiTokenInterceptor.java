package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.integration.FootballDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FootballDataApiTokenInterceptor implements ClientHttpRequestInterceptor {

    private final FootballDataService footballDataService;

    public FootballDataApiTokenInterceptor(FootballDataService footballDataService) {
        this.footballDataService = footballDataService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        final String apiToken = footballDataService.loadSettings().getApiToken();
        if (StringUtils.isNotBlank(apiToken)) {
            request.getHeaders().add("X-Auth-Token", apiToken);
            return execution.execute(request, body);
        }

        return execution.execute(request, body);
    }
}
