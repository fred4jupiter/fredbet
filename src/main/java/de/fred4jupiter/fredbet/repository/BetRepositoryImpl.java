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
    public Map<Long, PointCourseResultSimple> fetchPointCourseResultSimple() {
        final String sql = """
                select m.match_id, b.user_name, b.points 
                from matches m join bet b on m.match_id = b.match_id 
                order by m.kick_off_date;                                
                """;
        LOG.debug("sql={}", sql);
        final Map<Long, PointCourseResultSimple> map = new HashMap<>();

        namedParameterJdbcOperations.query(sql, (ResultSet rs) -> {
            Long matchId = rs.getLong(1);
            PointCourseResultSimple entry = new PointCourseResultSimple(matchId, rs.getString(2), rs.getInt(3));
            map.put(matchId, entry);
        });
        return map;
    }

    @Override
    public List<UsernamePoints> calculateRanging() {
        StringBuilder builder = new StringBuilder();
        builder.append("Select user_name, sum(total) as sum_all from (");
        builder.append("(Select user_name, sum(points) as total ");
        builder.append("from bet where user_name not like :username group by user_name order by total desc) ");
        builder.append("union all ");
        builder.append("(Select user_name, sum(points_one + points_two + points_three) as total ");
        builder.append("from extra_bet where user_name not like :username group by user_name order by total desc) ");
        builder.append(") as complete ");
        builder.append("group by user_name order by sum_all desc");

        Map<String, Object> params = new HashMap<>();
        params.put("username", FredbetConstants.TECHNICAL_USERNAME);

        final String sql = builder.toString();
        LOG.debug("sql={}", sql);
        return namedParameterJdbcOperations.query(sql, params, (ResultSet rs, int rowNum) -> {
            UsernamePoints usernamePoints = new UsernamePoints();
            usernamePoints.setUserName(rs.getString(1));
            usernamePoints.setTotalPoints(rs.getInt(2));
            return usernamePoints;
        });
    }

}
