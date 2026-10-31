package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class ExcelExportControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void showAllExportsAsDownloadLinks() {
        assertThat(mvc.get().uri("/excelexport/show"))
            .hasStatusOk()
            .bodyText().contains(
                "href=\"/excelexport/allBets\"",
                "href=\"/excelexport/extraBets\"",
                "href=\"/excelexport/ranking\"",
                "href=\"/excelexport/pointsCount\"",
                "All extra bets",
                "Ranking");
    }
}
