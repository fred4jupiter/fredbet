package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.service.InfoService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.service.user.UserService;
import de.fred4jupiter.fredbet.web.info.InfoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class DatabaseInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final InfoService infoService;

    private final StaticResourceLoader staticResourceLoader;

    private final ImageAdministrationService imageAdministrationService;

    private final UserService userService;

    private final FredbetProperties fredbetProperties;

    public DatabaseInitializer(InfoService infoService, StaticResourceLoader staticResourceLoader,
                               ImageAdministrationService imageAdministrationService,
                               UserService userService, FredbetProperties fredbetProperties) {
        this.infoService = infoService;
        this.staticResourceLoader = staticResourceLoader;
        this.imageAdministrationService = imageAdministrationService;
        this.userService = userService;
        this.fredbetProperties = fredbetProperties;
    }

    public void initDatabase() {
        imageAdministrationService.createDefaultImageGroup();

        imageAdministrationService.initUserProfileImageGroup();

        createAdminUser();
        addRulesIfEmpty();
    }

    private void addRulesIfEmpty() {
        String rulesInGerman = staticResourceLoader.loadDefaultRules();
        infoService.saveInfoContentIfNotPresent(InfoType.RULES, rulesInGerman, "de");
    }

    private void createAdminUser() {
        AppUser appUser = AppUserBuilder.create()
                .withUsernameAndPassword(fredbetProperties.adminUsername(), fredbetProperties.adminPassword())
                .withUserGroup(FredBetUserGroup.ROLE_ADMIN)
                .deletable(false)
                .build();
        boolean created = userService.saveUserIfNotExists(appUser);
        LOG.info("created new admin user: {}", created);
    }
}
