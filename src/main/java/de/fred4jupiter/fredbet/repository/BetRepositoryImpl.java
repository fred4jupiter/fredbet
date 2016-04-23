package de.fred4jupiter.fredbet.repository;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import de.fred4jupiter.fredbet.FredbetConstants;

class BetRepositoryImpl implements BetRepositoryCustom {

	@Autowired
	private NamedParameterJdbcOperations namedParameterJdbcOperations;

	public List<UsernamePoints> calculateRanging() {
		StringBuilder builder = new StringBuilder();
		builder.append("Select user_name, sum(total) as sum_all from (");
		builder.append("(Select user_name, sum(points) as total from bet where user_name not like :username group by user_name order by total desc) ");
		builder.append("union all ");
		builder.append("(Select user_name, sum(points) as total from extra_bet where user_name not like :username group by user_name order by total desc) ");
		builder.append(") as complete ");
		builder.append("group by user_name order by sum_all desc");
		
		Map<String, Object> params = new HashMap<>();
		params.put("username", FredbetConstants.TECHNICAL_USERNAME);
		
		return namedParameterJdbcOperations.query(builder.toString(), params, (ResultSet rs, int rowNum) -> {
			UsernamePoints usernamePoints = new UsernamePoints();
			usernamePoints.setUserName(rs.getString(1));
			usernamePoints.setTotalPoints(rs.getInt(2));
			return usernamePoints;
		});
	}
}
