package de.fred4jupiter.fredbet.excel;

import de.fred4jupiter.fredbet.Application;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.user.UserService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles(value = {FredBetProfile.DEV, FredBetProfile.INTEGRATION_TEST})
public class ReportServiceMT {

    @Autowired
    private ReportService reportService;

    @Autowired
    private DatabasePopulator dataBasePopulator;

    @Autowired
    private UserService userService;

    @Test
    public void exportResultsToExcel() throws IOException {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("fred", "feuerstein").withUserGroup(FredBetUserGroup.ROLE_USER).build();
        userService.createUserIfNotExists(appUser);

        dataBasePopulator.createRandomMatches();
        dataBasePopulator.createDemoBetsForAllUsers();
        dataBasePopulator.createDemoResultsForAllMatches();

        File file = new File("d://Temp1/export.xlsx");

        byte[] export = reportService.exportBetsToExcel(Locale.GERMAN);

        IOUtils.write(export, new FileOutputStream(file));
    }
}
