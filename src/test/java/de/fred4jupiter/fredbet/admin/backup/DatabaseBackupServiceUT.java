package de.fred4jupiter.fredbet.admin.backup;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class DatabaseBackupServiceUT {

    @Mock
    private DatabaseBackupRepository databaseBackupRepository;

    @Mock
    private RuntimeSettingsRepository runtimeSettingsRepository;

    @Mock
    private Environment environment;

    @Test
    public void executeBackupRejectsUnsupportedDatabaseDriver() {
        when(environment.getProperty("spring.datasource.driver-class-name")).thenReturn("org.postgresql.Driver");

        assertThatThrownBy(() -> createService().executeBackup())
            .isInstanceOf(DatabaseBackupCreationException.class)
            .extracting("errorCode")
            .isEqualTo(DatabaseBackupCreationException.ErrorCode.UNSUPPORTED_DATABASE_TYPE);
    }

    @Test
    public void executeBackupRejectsInMemoryDatabase() {
        when(environment.getProperty("spring.datasource.driver-class-name")).thenReturn("org.h2.Driver");
        when(environment.getProperty("spring.datasource.url")).thenReturn("jdbc:h2:mem:testdb");

        assertThatThrownBy(() -> createService().executeBackup())
            .isInstanceOf(DatabaseBackupCreationException.class)
            .extracting("errorCode")
            .isEqualTo(DatabaseBackupCreationException.ErrorCode.IN_MEMORY_NOT_SUPPORTED);
    }

    @Test
    public void loadDatabaseBackupReturnsStoredSettingsOrDefaultFolder() {
        when(runtimeSettingsRepository.loadRuntimeSettings(2L, DatabaseBackup.class)).thenReturn(new DatabaseBackup("/tmp/backups"));

        assertThat(createService().loadDatabaseBackup().databaseBackupFolder()).isEqualTo("/tmp/backups");

        when(runtimeSettingsRepository.loadRuntimeSettings(2L, DatabaseBackup.class)).thenReturn(null);

        assertThat(createService().loadDatabaseBackup().databaseBackupFolder()).contains("fredbet");
    }

    @Test
    public void saveBackupFolderPersistsRuntimeSettings() {
        createService().saveBackupFolder("/backup/folder");

        verify(runtimeSettingsRepository).saveRuntimeSettings(2L, new DatabaseBackup("/backup/folder"));
    }

    private DatabaseBackupService createService() {
        return new DatabaseBackupService(databaseBackupRepository, runtimeSettingsRepository, environment);
    }
}

