package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.FootballDataProperties;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsRepository;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FootballDataService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataService.class);

    private final RuntimeSettingsRepository runtimeSettingsRepository;

    private final RuntimeSettingsService runtimeSettingsService;

    private final FredbetProperties fredbetProperties;

    FootballDataService(RuntimeSettingsRepository runtimeSettingsRepository, RuntimeSettingsService runtimeSettingsService, FredbetProperties fredbetProperties) {
        this.runtimeSettingsRepository = runtimeSettingsRepository;
        this.runtimeSettingsService = runtimeSettingsService;
        this.fredbetProperties = fredbetProperties;
    }

    @Cacheable(CacheNames.FOOTBALL_DATA_SETTINGS)
    public FootballDataRuntimeSettings loadSettings() {
        LOG.debug("Loading FootballDataSettings ...");
        final FootballDataRuntimeSettings footballDataRuntimeSettings = runtimeSettingsRepository.loadRuntimeSettings(FootballDataRuntimeSettings.ID, FootballDataRuntimeSettings.class);
        if (footballDataRuntimeSettings == null) {
            LOG.debug("No FootballDataSettings found. Creating default settings.");
            FootballDataRuntimeSettings settings = new FootballDataRuntimeSettings();
            settings.setEnabled(false);
            addApiTokenFromPropertiesIfAvailable(settings);
            saveSettings(settings);
            return settings;
        }
        addApiTokenFromPropertiesIfAvailable(footballDataRuntimeSettings);
        return footballDataRuntimeSettings;
    }

    private void addApiTokenFromPropertiesIfAvailable(FootballDataRuntimeSettings settings) {
        final FootballDataProperties footballDataProperties = fredbetProperties.integration().footballData();
        if (footballDataProperties != null && StringUtils.isNotBlank(footballDataProperties.apiToken())) {
            settings.setApiToken(footballDataProperties.apiToken());
        }
    }

    @CacheEvict(cacheNames = CacheNames.FOOTBALL_DATA_SETTINGS, allEntries = true)
    public void saveSettings(FootballDataRuntimeSettings settings) {
        if (settings.isEnabled()) {
            final RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
            runtimeSettings.setTeamBundle(TeamBundle.FOOTBALL_DATA_USAGE);
            runtimeSettingsService.saveRuntimeSettings(runtimeSettings);
        }
        this.runtimeSettingsRepository.saveRuntimeSettings(FootballDataRuntimeSettings.ID, settings);
    }
}
