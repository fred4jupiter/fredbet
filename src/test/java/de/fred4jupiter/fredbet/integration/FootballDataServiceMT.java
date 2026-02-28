package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class FootballDataServiceMT {

    @Autowired
    private FootballDataService footballDataService;

    @Test
    void fetchData() {
        footballDataService.importData();
    }
}
