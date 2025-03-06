package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PointsConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(PointsConfigService.class);

    private static final Long POINTS_CONFIG_ID = 2L;

    private final RuntimeSettingsRepository runtimeSettingsRepository;

    public PointsConfigService(RuntimeSettingsRepository runtimeSettingsRepository) {
        this.runtimeSettingsRepository = runtimeSettingsRepository;
    }

    @Cacheable(CacheNames.POINTS_CONFIG)
    public PointsConfiguration loadPointsConfig() {
        LOG.debug("loading points config...");
        PointsConfiguration pointsConfig = runtimeSettingsRepository.loadRuntimeSettings(POINTS_CONFIG_ID, PointsConfiguration.class);
        return pointsConfig == null ? createDefaultPointsConfig() : pointsConfig;
    }

    @CacheEvict(cacheNames = CacheNames.POINTS_CONFIG, allEntries = true)
    public void savePointsConfig(PointsConfiguration pointsConfig) {
        runtimeSettingsRepository.saveRuntimeSettings(POINTS_CONFIG_ID, pointsConfig);
    }

    public PointsConfiguration createDefaultPointsConfig() {
        PointsConfiguration pointsConfig = new PointsConfiguration();

        pointsConfig.setPointsCorrectResult(3);
        pointsConfig.setPointsSameGoalDifference(2);
        pointsConfig.setPointsCorrectWinner(1);
        pointsConfig.setPointsCorrectNumberOfGoalsOneTeam(0);

        ExtraPointsConfiguration extraPointsConfig = new ExtraPointsConfiguration();
        extraPointsConfig.setPointsFinalWinner(10);
        extraPointsConfig.setPointsSemiFinalWinner(5);
        extraPointsConfig.setPointsThirdFinalWinner(2);
        pointsConfig.setExtraPointsConfig(extraPointsConfig);
        return pointsConfig;
    }
}
