package de.fred4jupiter.fredbet.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class BetRepositoryImpl implements BetRepositoryCustom {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<UsernamePoints> calculateRanging() {
		final String sql = "Select user_name, sum(points) as total from bet group by user_name order by total desc";

		return namedParameterJdbcTemplate.query(sql, new RowMapper<UsernamePoints>() {

			@Override
			public UsernamePoints mapRow(ResultSet rs, int rowNum) throws SQLException {
				UsernamePoints usernamePoints = new UsernamePoints();
				usernamePoints.setUserName(rs.getString(1));
				usernamePoints.setTotalPoints(rs.getInt(2));
				return usernamePoints;
			}
		});
	}
}
