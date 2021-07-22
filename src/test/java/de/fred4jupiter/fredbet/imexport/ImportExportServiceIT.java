package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
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

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private ExtraBetRepository extraBetRepository;

    @Test
    public void exportAllAsJsonAndImportAgain() {
        final int numberOfDemoUsers = 10;
        databasePopulator.createDemoUsers(numberOfDemoUsers);
        databasePopulator.createRandomMatches();
        databasePopulator.createDemoBetsForAllUsers();
        databasePopulator.createDemoResultsForAllMatches();

        String json = importExportService.exportAllToJson();
        boolean result = TempFileWriterUtil.writeToTempFolder(json.getBytes(), "fredbet_export.json");
        assertThat(result).isTrue();

        importExportService.importAllFromJson(json);

        assertThat(appUserRepository.count()).isGreaterThanOrEqualTo(numberOfDemoUsers);
        assertThat(matchRepository.count()).isGreaterThanOrEqualTo(48);
        assertThat(betRepository.count()).isGreaterThanOrEqualTo(numberOfDemoUsers * 48);
        assertThat(extraBetRepository.count()).isGreaterThanOrEqualTo(numberOfDemoUsers);
    }
}
