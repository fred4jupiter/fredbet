package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
// info is not exposed by default; it stands in for any actuator endpoint that is exposed later
@TestPropertySource(properties = "management.endpoints.web.exposure.include=health,info")
public class SecurityConfigIT {

    private static final Pattern VERSIONED_LOGIN_CSS = Pattern.compile("/css/pages/login-[0-9a-f]{32}\\.css");

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void pagesAreNotCached() {
        assertThat(mvc.get().uri("/login"))
            .hasStatusOk()
            .headers().hasValue(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
    }

    @Test
    public void staticResourcesAreVersionedAndCachedLong() throws Exception {
        MvcTestResult loginPage = mvc.get().uri("/login").exchange();
        Matcher matcher = VERSIONED_LOGIN_CSS.matcher(loginPage.getResponse().getContentAsString());
        assertThat(matcher.find()).as("login page references the content versioned css").isTrue();

        assertThat(mvc.get().uri(matcher.group()))
            .hasStatusOk()
            .headers().hasValue(HttpHeaders.CACHE_CONTROL, "max-age=31536000");
    }

    @Test
    public void healthEndpointIsPublic() {
        assertThat(mvc.get().uri("/actuator/health")).hasStatusOk();
    }

    @Test
    @WithMockUser
    public void otherActuatorEndpointsAreForbiddenForNormalUsers() {
        assertThat(mvc.get().uri("/actuator/info")).hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    @WithMockUser(authorities = FredBetPermission.PERM_ADMINISTRATION)
    public void otherActuatorEndpointsAreAllowedWithAdministrationPermission() {
        assertThat(mvc.get().uri("/actuator/info")).hasStatusOk();
    }
}
