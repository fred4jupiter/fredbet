package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class PointsConfigServiceUT {

    @InjectMocks
    private PointsConfigService pointsConfigService;

    @Mock
    private RuntimeSettingsRepository runtimeSettingsRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private CacheAdministrationService cacheAdministrationService;

    @Test
    public void loadPointsConfigWithDefaultsWhenRepositoryReturnsNull() {
        when(runtimeSettingsRepository.loadRuntimeSettings(eq(2L), eq(PointsConfiguration.class))).thenReturn(null);

        PointsConfiguration pointsConfig = pointsConfigService.loadPointsConfig();

        assertThat(pointsConfig).isNotNull();
        assertThat(pointsConfig.getPointsCorrectResult()).isEqualTo(3);
        assertThat(pointsConfig.getPointsSameGoalDifference()).isEqualTo(2);
        assertThat(pointsConfig.getPointsCorrectWinner()).isEqualTo(1);
        assertThat(pointsConfig.getPointsCorrectNumberOfGoalsOneTeam()).isEqualTo(0);
    }

    @Test
    public void loadPointsConfigReturnsStoredConfigWhenAvailable() {
        PointsConfiguration storedPointsConfig = new PointsConfiguration();
        storedPointsConfig.setPointsCorrectResult(6);

        when(runtimeSettingsRepository.loadRuntimeSettings(eq(2L), eq(PointsConfiguration.class))).thenReturn(storedPointsConfig);

        PointsConfiguration pointsConfig = pointsConfigService.loadPointsConfig();

        assertThat(pointsConfig).isSameAs(storedPointsConfig);
    }

    @Test
    public void savePointsConfigStoresAndClearsCacheBeforePublishingEvent() {
        PointsConfiguration pointsConfig = PointsConfiguration.withDefaults();

        pointsConfigService.savePointsConfig(pointsConfig);

        InOrder inOrder = inOrder(runtimeSettingsRepository, cacheAdministrationService, applicationEventPublisher);

        inOrder.verify(runtimeSettingsRepository).saveRuntimeSettings(2L, pointsConfig);
        inOrder.verify(cacheAdministrationService).clearCacheByCacheName(CacheNames.POINTS_CONFIG);
        inOrder.verify(applicationEventPublisher).publishEvent(any(PointsConfigurationChangedEvent.class));
        verify(applicationEventPublisher).publishEvent(any(PointsConfigurationChangedEvent.class));
    }
}

