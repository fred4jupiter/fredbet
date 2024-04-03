package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.util.TimeZoneUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Central service for initialization after application startup.
 */
@Component
public class PostApplicationStartedChecker {

    private static final Logger LOG = LoggerFactory.getLogger(PostApplicationStartedChecker.class);

    private final DatabasePopulator databasePopulator;

    private final RuntimeSettingsService runtimeSettingsService;

    private final ImageAdministrationService imageAdministrationService;

    private final TimeZoneUtil timeZoneUtil;

    public PostApplicationStartedChecker(DatabasePopulator databasePopulator, RuntimeSettingsService runtimeSettingsService,
                                         ImageAdministrationService imageAdministrationService, TimeZoneUtil timeZoneUtil) {
        this.databasePopulator = databasePopulator;
        this.runtimeSettingsService = runtimeSettingsService;
        this.imageAdministrationService = imageAdministrationService;
        this.timeZoneUtil = timeZoneUtil;
    }

    @EventListener
    public void createDemoData(ApplicationReadyEvent event) {
        WebApplicationType webApplicationType = event.getSpringApplication().getWebApplicationType();
        if (!WebApplicationType.SERVLET.equals(webApplicationType)) {
            return;
        }

        LOG.debug("checking initialization...");

        databasePopulator.initDatabaseWithDemoData();

        ajustTimezone();

        imageAdministrationService.initUserProfileImageGroup();
    }

    private void ajustTimezone() {
        final RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        timeZoneUtil.checkIfTimezoneIsCorrect(runtimeSettings.getTimeZone());
    }
}
