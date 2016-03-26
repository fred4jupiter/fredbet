package de.fred4jupiter.fredbet.repository;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import de.fred4jupiter.fredbet.FredbetConstants;

class BetRepositoryImpl implements BetRepositoryCustom {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<UsernamePoints> calculateRanging() {
		final String sql = "Select user_name, sum(points) as total from bet where user_name not like ? group by user_name order by total desc";

		return jdbcTemplate.query(sql, new Object[] { FredbetConstants.TECHNICAL_USERNAME }, (ResultSet rs, int rowNum) -> {
			UsernamePoints usernamePoints = new UsernamePoints();
			usernamePoints.setUserName(rs.getString(1));
			usernamePoints.setTotalPoints(rs.getInt(2));
			return usernamePoints;
		});
	}
}
