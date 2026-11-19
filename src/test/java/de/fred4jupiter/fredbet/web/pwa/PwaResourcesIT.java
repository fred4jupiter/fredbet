package de.fred4jupiter.fredbet.web.pwa;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The files of the installable web app (PWA) must be reachable without login, otherwise the browser
 * can not install the app from the login page and Spring Security would redirect to them after login.
 */
@MockMvcIntegrationTest
public class PwaResourcesIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void manifestIsPublicAndNotCachedLong() {
        assertThat(mvc.get().uri("/manifest.json")).hasStatusOk()
                .hasContentTypeCompatibleWith("application/json")
                .hasHeader("Cache-Control", "no-cache")
                .bodyText()
                .contains("\"name\": \"FredBet\"", "\"display\": \"standalone\"", "pwa/icon-512.png");
    }

    @Test
    public void serviceWorkerIsPublicAndHasNoFetchHandler() {
        assertThat(mvc.get().uri("/sw.js")).hasStatusOk()
                .hasHeader("Cache-Control", "no-cache")
                .bodyText()
                .contains("install", "activate")
                .doesNotContain("'fetch'", "caches.");
    }

    @Test
    public void iconsArePublic() {
        assertThat(mvc.get().uri("/pwa/icon-192.png")).hasStatusOk();
        assertThat(mvc.get().uri("/pwa/icon-maskable-512.png")).hasStatusOk();
        assertThat(mvc.get().uri("/pwa/apple-touch-icon.png")).hasStatusOk();
    }

    @Test
    public void loginPageLinksManifestAndContainsInstallHint() {
        assertThat(mvc.get().uri("/login")).hasStatusOk().bodyText()
                .contains("rel=\"manifest\" href=\"/manifest.json\"",
                        "rel=\"apple-touch-icon\"",
                        "name=\"theme-color\"",
                        "id=\"pwaInstallHint\"",
                        "data-sw-url=\"/sw.js\"",
                        "/js/pwa_install-");
    }
}
