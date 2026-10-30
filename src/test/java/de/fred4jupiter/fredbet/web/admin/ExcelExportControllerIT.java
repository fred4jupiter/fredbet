package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class ExcelExportControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showAllExportsAsDownloadLinks() throws Exception {

        mockMvc.perform(get("/excelexport/show"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("href=\"/excelexport/allBets\""),
                containsString("href=\"/excelexport/extraBets\""),
                containsString("href=\"/excelexport/ranking\""),
                containsString("href=\"/excelexport/pointsCount\""),
                containsString("All extra bets"),
                containsString("Ranking"))));
    }
}
