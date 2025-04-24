package de.fred4jupiter.fredbet.excel;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.data.DemoDataCreation;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.user.UserService;
import de.fred4jupiter.fredbet.util.TempFileWriterUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

@IntegrationTest
public class ReportServiceMT {

    @Autowired
    private ReportService reportService;

    @Autowired
    private DatabasePopulator dataBasePopulator;

    @Autowired
    private UserService userService;

    @Test
    public void exportResultsToExcel() {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("fred", "feuerstein").withUserGroup(FredBetUserGroup.ROLE_USER).build();
        userService.createUserIfNotExists(appUser);

        dataBasePopulator.createDemoData();

        byte[] export = reportService.exportBetsToExcel(Locale.GERMAN);

        TempFileWriterUtil.writeToTempFolder(export, "export.xlsx");
    }
}
