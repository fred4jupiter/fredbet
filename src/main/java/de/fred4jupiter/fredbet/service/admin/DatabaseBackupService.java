package de.fred4jupiter.fredbet.service.admin;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.DatabaseBackup;
import de.fred4jupiter.fredbet.props.DatabaseType;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.DatabaseBackupRepository;
import de.fred4jupiter.fredbet.repository.RuntimeConfigRepository;
import de.fred4jupiter.fredbet.service.admin.DatabaseBackupCreationException.ErrorCode;

@Service
public class DatabaseBackupService {

	@Autowired
	private DatabaseBackupRepository databaseBackupRepository;

	@Autowired
	private RuntimeConfigRepository<DatabaseBackup> runtimeConfigRepository;

	private static final Long DATABASE_BACKUP_CONFIG_ID = Long.valueOf(2);

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
		DatabaseBackup databaseBackup = runtimeConfigRepository.loadRuntimeConfig(DATABASE_BACKUP_CONFIG_ID, DatabaseBackup.class);
		if (databaseBackup == null) {
			databaseBackup = new DatabaseBackup();
			databaseBackup.setDatabaseBackupFolder(determineDefaultBackupFolder());
		}
		return databaseBackup;
	}

	public void saveBackupFolder(String backupFolder) {
		DatabaseBackup databaseBackup = loadDatabaseBackup();
		databaseBackup.setDatabaseBackupFolder(backupFolder);
		runtimeConfigRepository.saveRuntimeConfig(DATABASE_BACKUP_CONFIG_ID, databaseBackup);
	}
}
