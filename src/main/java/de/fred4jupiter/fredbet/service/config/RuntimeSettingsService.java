package de.fred4jupiter.fredbet.service.config;

import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.repository.RuntimeSettingsRepository;
import de.fred4jupiter.fredbet.service.admin.TimeZoneService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.TimeZone;

/**
 * Service for loading runtime configuration settings.
 *
 * @author michael
 */
@Service
@Transactional
public class RuntimeSettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeSettingsService.class);

    private static final Long DEFAULT_CONFIG_ID = 1L;

    private final RuntimeSettingsRepository runtimeSettingsRepository;

    private final Environment environment;

    private final TimeZoneService timeZoneService;

    public RuntimeSettingsService(RuntimeSettingsRepository runtimeSettingsRepository,
                                  Environment environment, TimeZoneService timeZoneService) {
        this.runtimeSettingsRepository = runtimeSettingsRepository;
        this.environment = environment;
        this.timeZoneService = timeZoneService;
    }

    @Cacheable(CacheNames.RUNTIME_SETTINGS)
    public RuntimeSettings loadRuntimeSettings() {
        LOG.debug("Loading runtime settings from DB...");
        RuntimeSettings runtimeSettings = runtimeSettingsRepository.loadRuntimeSettings(DEFAULT_CONFIG_ID, RuntimeSettings.class);
        return runtimeSettings == null ? createDefaultRuntimeSettings() : runtimeSettings;
    }

    private RuntimeSettings createDefaultRuntimeSettings() {
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        // set defaults
        runtimeSettings.setFavouriteCountry(FredbetConstants.DEFAULT_FAVOURITE_COUNTRY);
        runtimeSettings.setPasswordForReset(FredbetConstants.DEFAULT_REST_PASSWORT);
        runtimeSettings.setChangePasswordOnFirstLogin(true);
        runtimeSettings.setSelfRegistrationEnabled(false);
        runtimeSettings.setRegistrationCode(RandomStringUtils.randomAlphanumeric(6));
        runtimeSettings.setTimeZone(TimeZone.getDefault().getID());

        if (environment.acceptsProfiles(Profiles.of(FredBetProfile.DEV, FredBetProfile.H2))) {
            runtimeSettings.setShowDemoDataNavigationEntry(true);
            runtimeSettings.setEnableChangingUsername(true);
            saveRuntimeSettings(runtimeSettings);
        }

        return runtimeSettings;
    }

    @CacheEvict(cacheNames = CacheNames.RUNTIME_SETTINGS, allEntries = true)
    public void saveRuntimeSettings(RuntimeSettings runtimeSettings) {
        if (StringUtils.isNotBlank(runtimeSettings.getTimeZone())) {
            timeZoneService.setTimeZone(runtimeSettings.getTimeZone());
        }

        runtimeSettingsRepository.saveRuntimeSettings(DEFAULT_CONFIG_ID, runtimeSettings);
    }
}
