package de.fred4jupiter.fredbet.service.config;

import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.repository.RuntimeSettingsRepository;
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

    public RuntimeSettingsService(RuntimeSettingsRepository runtimeSettingsRepository, Environment environment) {
        this.runtimeSettingsRepository = runtimeSettingsRepository;
        this.environment = environment;
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
        runtimeSettings.setPasswordForReset(FredbetConstants.DEFAULT_RESET_PASSWORT);
        runtimeSettings.setChangePasswordOnFirstLogin(true);
        runtimeSettings.setSelfRegistrationEnabled(false);
        runtimeSettings.setRegistrationCode(RandomStringUtils.randomAlphanumeric(6));
        runtimeSettings.setTimeZone(getTimeZoneId());

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
            setTimeZone(runtimeSettings.getTimeZone());
        }

        runtimeSettingsRepository.saveRuntimeSettings(DEFAULT_CONFIG_ID, runtimeSettings);
    }

    public void checkIfTimezoneIsCorrect() {
        final RuntimeSettings runtimeSettings = loadRuntimeSettings();
        final String timeZoneId = runtimeSettings.getTimeZone();

        final String defaultTimeZone = getTimeZoneId();

        if (StringUtils.isNotBlank(timeZoneId) && timeZoneId.equals(defaultTimeZone)) {
            return;
        }

        LOG.debug("setting timezone to: {}", timeZoneId);
        setTimeZone(timeZoneId);
    }

    private void setTimeZone(String timeZoneId) {
        if (StringUtils.isBlank(timeZoneId)) {
            LOG.warn("timeZoneId is null");
            return;
        }
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        LOG.info("Setting timeZone to: {}", timeZone.getID());
        TimeZone.setDefault(timeZone);
    }

    private String getTimeZoneId() {
        String timeZoneId = TimeZone.getDefault().getID();
        return StringUtils.isNotBlank(timeZoneId) ? timeZoneId : FredbetConstants.DEFAULT_TIMEZONE;
    }
}
