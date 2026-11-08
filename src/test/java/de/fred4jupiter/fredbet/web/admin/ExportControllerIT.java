package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class ExportControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void showAllExportsAsDownloadLinks() {
        assertThat(mvc.get().uri("/export/show"))
            .hasStatusOk()
            .bodyText().contains(
                "href=\"/export/allBetsPdf\"",
                "href=\"/export/allBets\"",
                "href=\"/export/extraBets\"",
                "href=\"/export/ranking\"",
                "href=\"/export/pointsCount\"",
                "All matches with the bets of all users and the bonus bets",
                "All extra bets",
                "Ranking");
    }

    @Test
    public void exportAllBetsAsPdf() {
        assertThat(mvc.get().uri("/export/allBetsPdf"))
            .hasStatusOk()
            .hasContentTypeCompatibleWith(MediaType.APPLICATION_PDF)
            .hasHeader("Content-Disposition", "attachment; filename=\"allBets.pdf\"");
    }
}
