package de.fred4jupiter.fredbet.service.admin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.repository.DatabaseBackupRepository;

@Service
public class DatabaseBackupService {

	@Autowired
	private DatabaseBackupRepository databaseBackupRepository;

	public String executeBackup() {
		String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String pathFilename = "~/fredbet/" + formattedDateTime + "_fredbetdb.zip";
		databaseBackupRepository.executeBackup(pathFilename);
		return pathFilename;
	}
}
