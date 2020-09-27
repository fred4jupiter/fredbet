package de.fred4jupiter.fredbet.service.admin;

import de.fred4jupiter.fredbet.domain.DatabaseBackup;
import de.fred4jupiter.fredbet.repository.DatabaseBackupRepository;
import de.fred4jupiter.fredbet.repository.RuntimeSettingsRepository;
import de.fred4jupiter.fredbet.service.admin.DatabaseBackupCreationException.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseBackupService {

    private static final Long DATABASE_BACKUP_CONFIG_ID = 2L;

    private final DatabaseBackupRepository databaseBackupRepository;

    private final RuntimeSettingsRepository<DatabaseBackup> runtimeSettingsRepository;

    private final Environment environment;

    public DatabaseBackupService(DatabaseBackupRepository databaseBackupRepository,
                                 RuntimeSettingsRepository<DatabaseBackup> runtimeSettingsRepository, Environment environment) {
        this.databaseBackupRepository = databaseBackupRepository;
        this.runtimeSettingsRepository = runtimeSettingsRepository;
        this.environment = environment;
    }

    public String executeBackup() {
        String driverClassName = this.environment.getProperty("spring.datasource.hikari.driver-class-name");
        if (StringUtils.isNotBlank(driverClassName) && !driverClassName.contains("h2")) {
            throw new DatabaseBackupCreationException("Database of driver=" + driverClassName + " is not supported for backup!",
                    ErrorCode.UNSUPPORTED_DATABASE_TYPE);
        }

        String jdbcUrl = this.environment.getProperty("spring.datasource.hikari.jdbc-url");

        if (StringUtils.isNotBlank(jdbcUrl) && jdbcUrl.contains("h2:mem")) {
            throw new DatabaseBackupCreationException("Could not create a database backup of H2 in-memory databases!",
                    ErrorCode.IN_MEMORY_NOT_SUPPORTED);
        }

        String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        DatabaseBackup databaseBackup = loadDatabaseBackup();
        String fileName = formattedDateTime + "_fredbetdb_bkp.zip";
        String pathFilename = databaseBackup.getDatabaseBackupFolder() + File.separator + fileName;
        databaseBackupRepository.executeBackup(pathFilename);
        return pathFilename;
    }

    private String determineDefaultBackupFolder() {
        String userHomeFolder = System.getProperty("user.home");
        return userHomeFolder + File.separator + "fredbet";
    }

    public DatabaseBackup loadDatabaseBackup() {
        DatabaseBackup databaseBackup = runtimeSettingsRepository.loadRuntimeSettings(DATABASE_BACKUP_CONFIG_ID, DatabaseBackup.class);
        if (databaseBackup == null) {
            databaseBackup = new DatabaseBackup();
            databaseBackup.setDatabaseBackupFolder(determineDefaultBackupFolder());
        }
        return databaseBackup;
    }

    public void saveBackupFolder(String backupFolder) {
        DatabaseBackup databaseBackup = loadDatabaseBackup();
        databaseBackup.setDatabaseBackupFolder(backupFolder);
        runtimeSettingsRepository.saveRuntimeSettings(DATABASE_BACKUP_CONFIG_ID, databaseBackup);
    }
}
