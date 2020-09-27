package de.fred4jupiter.fredbet.repository;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseBackupRepository {

    private final JdbcOperations jdbcOperations;

    public DatabaseBackupRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void executeBackup(String pathFileName) {
        jdbcOperations.execute("BACKUP TO '" + pathFileName + "'");
    }
}
