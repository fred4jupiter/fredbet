package de.fred4jupiter.fredbet.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseBackupRepository {

	@Autowired
	private JdbcOperations jdbcOperations;

	public void executeBackup(String pathFileName) {
		jdbcOperations.execute("BACKUP TO '" + pathFileName + "'");
	}
}
