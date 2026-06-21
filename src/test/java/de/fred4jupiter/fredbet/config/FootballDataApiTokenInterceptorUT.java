package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.integration.FootballDataRuntimeSettings;
import de.fred4jupiter.fredbet.integration.FootballDataService;
import de.fred4jupiter.fredbet.props.FootballDataProperties;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.props.IntegrationProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.mock.http.client.MockClientHttpRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class FootballDataApiTokenInterceptorUT {

    @Mock
    private FootballDataService footballDataService;

    @Mock
    private ClientHttpRequestExecution clientHttpRequestExecution;

    @Mock
    private ClientHttpResponse clientHttpResponse;

    @Test
    public void interceptAddsRuntimeApiTokenToHeader() throws IOException {
        FootballDataRuntimeSettings runtimeSettings = new FootballDataRuntimeSettings();
        runtimeSettings.setApiToken("runtime-token");
        when(footballDataService.loadSettings()).thenReturn(runtimeSettings);
        when(clientHttpRequestExecution.execute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(clientHttpResponse);

        FootballDataApiTokenInterceptor interceptor = new FootballDataApiTokenInterceptor(footballDataService, fredbetProperties(null));
        MockClientHttpRequest request = new MockClientHttpRequest();

        interceptor.intercept(request, new byte[0], clientHttpRequestExecution);

        assertThat(request.getHeaders().getFirst("X-Auth-Token")).isEqualTo("runtime-token");
        verify(clientHttpRequestExecution).execute(request, new byte[0]);
    }

    @Test
    public void interceptFallsBackToConfiguredApiToken() throws IOException {
        FootballDataRuntimeSettings runtimeSettings = new FootballDataRuntimeSettings();
        when(footballDataService.loadSettings()).thenReturn(runtimeSettings);
        when(clientHttpRequestExecution.execute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(clientHttpResponse);

        FootballDataApiTokenInterceptor interceptor = new FootballDataApiTokenInterceptor(footballDataService, fredbetProperties("config-token"));
        MockClientHttpRequest request = new MockClientHttpRequest();

        interceptor.intercept(request, new byte[0], clientHttpRequestExecution);

        assertThat(request.getHeaders().getFirst("X-Auth-Token")).isEqualTo("config-token");
    }

    @Test
    public void interceptThrowsWhenNoApiTokenIsConfigured() {
        when(footballDataService.loadSettings()).thenReturn(new FootballDataRuntimeSettings());
        FootballDataApiTokenInterceptor interceptor = new FootballDataApiTokenInterceptor(footballDataService, fredbetProperties(" "));
        HttpRequest request = new HttpRequestWrapper(new MockClientHttpRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                return super.getHeaders();
            }
        };

        assertThatThrownBy(() -> interceptor.intercept(request, new byte[0], clientHttpRequestExecution))
            .isInstanceOf(FootballDataApiTokenNotConfiguredException.class);
    }

    private FredbetProperties fredbetProperties(String apiToken) {
        FootballDataProperties footballDataProperties = new FootballDataProperties("base", 1, apiToken, "crest");
        return new FredbetProperties("en", 1, 2, 3, "admin", "pw", null, null, new IntegrationProperties(footballDataProperties));
    }
}

