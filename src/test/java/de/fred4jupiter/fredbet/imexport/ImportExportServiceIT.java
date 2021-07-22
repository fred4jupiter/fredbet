package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.util.TempFileWriterUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class ImportExportServiceIT {

    @Autowired
    private ImportExportService importExportService;

    @Autowired
    private DatabasePopulator databasePopulator;

    @Test
    public void exportAllAsJsonAndImportAgain() {
        databasePopulator.createDemoUsers(10);
        databasePopulator.createRandomMatches();
        databasePopulator.createDemoBetsForAllUsers();
        databasePopulator.createDemoResultsForAllMatches();

        String json = importExportService.exportAllToJson();
        boolean result = TempFileWriterUtil.writeToTempFolder(json.getBytes(), "fredbet_export.json");
        assertThat(result).isTrue();

        importExportService.importAllFromJson(json);

    }
}
