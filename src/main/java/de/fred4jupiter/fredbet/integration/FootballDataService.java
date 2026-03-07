package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.integration.model.FdCompetition;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FootballDataService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataService.class);

    private final RuntimeSettingsRepository runtimeSettingsRepository;

    private final FootballDataRestClient footballDataRestClient;

    FootballDataService(RuntimeSettingsRepository runtimeSettingsRepository, FootballDataRestClient footballDataRestClient) {
        this.runtimeSettingsRepository = runtimeSettingsRepository;
        this.footballDataRestClient = footballDataRestClient;
    }

    public List<Competition> loadCompetitions() {
        List<FdCompetition> fdCompetitions = footballDataRestClient.fetchCompetitions();
        return fdCompetitions.stream()
            .map(fdCompetition -> new Competition(fdCompetition.id(), fdCompetition.name(),
                fdCompetition.code(), fdCompetition.currentSeason().getSeasonYear()))
            .toList();
    }

    @Cacheable(CacheNames.FOOTBALL_DATA_SETTINGS)
    public FootballDataSettings loadSettings() {
        LOG.debug("Loading FootballDataSettings ...");
        return runtimeSettingsRepository.loadRuntimeSettings(FootballDataSettings.ID, FootballDataSettings.class);
    }

    @CacheEvict(cacheNames = CacheNames.FOOTBALL_DATA_SETTINGS, allEntries = true)
    public void saveSettings(FootballDataSettings footballDataSettings) {
        this.runtimeSettingsRepository.saveRuntimeSettings(FootballDataSettings.ID, footballDataSettings);
    }

}
