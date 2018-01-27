package de.fred4jupiter.fredbet.domain;

public class DatabaseBackup {

	/**
	 * Backup folder for H2 database backups.
	 */
	private String databaseBackupFolder;

	public String getDatabaseBackupFolder() {
		return databaseBackupFolder;
	}

	public void setDatabaseBackupFolder(String databaseBackupFolder) {
		this.databaseBackupFolder = databaseBackupFolder;
	}

}
