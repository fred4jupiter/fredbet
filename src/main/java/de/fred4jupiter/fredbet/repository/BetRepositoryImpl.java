package de.fred4jupiter.fredbet.repository;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class BetRepositoryImpl implements BetRepositoryCustom {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<UsernamePoints> calculateRanging() {
        final String sql = "Select user_name, sum(points) as total from bet group by user_name order by total desc";

        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> {
            UsernamePoints usernamePoints = new UsernamePoints();
            usernamePoints.setUserName(rs.getString(1));
            usernamePoints.setTotalPoints(rs.getInt(2));
            return usernamePoints;
        });
    }
}
