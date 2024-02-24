package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BetRepositoryImpl implements BetRepositoryCustom {

    private static final Logger LOG = LoggerFactory.getLogger(BetRepositoryImpl.class);

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public BetRepositoryImpl(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public List<UsernamePoints> calculateRanging() {
        final String sql = """
                Select user_name, sum(total) as sum_all 
                from (
                  (Select user_name, sum(points) as total
                   from bet 
                   where user_name not like :username 
                   group by user_name order by total desc)
                union all
                  (Select user_name, sum(points_one + points_two + points_three) as total
                   from extra_bet 
                   where user_name not like :username 
                   group by user_name order by total desc)
                  ) as complete
                group by user_name order by sum_all desc
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("username", FredbetConstants.TECHNICAL_USERNAME);

        LOG.debug("sql={}", sql);
        return namedParameterJdbcOperations.query(sql, params, (ResultSet rs, int rowNum) -> {
            UsernamePoints usernamePoints = new UsernamePoints();
            usernamePoints.setUserName(rs.getString(1));
            usernamePoints.setTotalPoints(rs.getInt(2));
            return usernamePoints;
        });
    }

}
