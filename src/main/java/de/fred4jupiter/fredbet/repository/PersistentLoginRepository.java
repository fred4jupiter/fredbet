package de.fred4jupiter.fredbet.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PersistentLoginRepository {

	@Autowired
	private NamedParameterJdbcOperations namedParameterJdbcOperations;

	@Transactional
	public void renameUser(String oldUsername, String newUsername) {
		final String sql = "UPDATE PERSISTENT_LOGINS SET USERNAME = :newUsername WHERE USERNAME = :oldUsername";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("oldUsername", oldUsername);
		params.addValue("newUsername", newUsername);

		namedParameterJdbcOperations.update(sql, params);
	}

}
