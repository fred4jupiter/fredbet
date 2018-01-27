package de.fred4jupiter.fredbet.service.admin;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.props.DatabaseType;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.DatabaseBackupRepository;
import de.fred4jupiter.fredbet.service.admin.DatabaseBackupCreationException.ErrorCode;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;

@Service
public class DatabaseBackupService {

	@Autowired
	private DatabaseBackupRepository databaseBackupRepository;

	@Autowired
	private RuntimeConfigurationService runtimeConfigurationService;

	@Autowired
	private FredbetProperties fredbetProperties;

	public String executeBackup() {
		DatabaseType databaseType = fredbetProperties.getDatabaseType();
		if (!DatabaseType.H2.equals(databaseType)) {
			throw new DatabaseBackupCreationException("Database of type=" + databaseType + " is not supported for backup!",
					ErrorCode.UNSUPPORTED_DATABASE_TYPE);
		}

		String databaseUrl = fredbetProperties.getDatabaseUrl();
		if (databaseUrl.contains("h2:mem")) {
			throw new DatabaseBackupCreationException("Could not create a database backup of H2 in-memory databases!",
					ErrorCode.IN_MEMORY_NOT_SUPPORTED);
		}

		String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

		String backupFolder = loadBackupFolder();
		if (StringUtils.isBlank(backupFolder)) {
			backupFolder = determineDefaultBackupFolder();
		}
		String fileName = formattedDateTime + "_fredbetdb_bkp.zip";
		String pathFilename = backupFolder + File.separator + fileName;
		databaseBackupRepository.executeBackup(pathFilename);
		return pathFilename;
	}

	private String determineDefaultBackupFolder() {
		String userHomeFolder = System.getProperty("user.home");
		return userHomeFolder + File.separator + "fredbet";
	}

	public String loadBackupFolder() {
		RuntimeConfig runtimeConfig = runtimeConfigurationService.loadRuntimeConfig();
		return runtimeConfig.getDatabaseBackupFolder();
	}

	public void saveBackupFolder(String databaseBackupFolder) {
		RuntimeConfig runtimeConfig = runtimeConfigurationService.loadRuntimeConfig();
		runtimeConfig.setDatabaseBackupFolder(databaseBackupFolder);
		runtimeConfigurationService.saveRuntimeConfig(runtimeConfig);
	}
}
