package de.fred4jupiter.fredbet.statistic;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class StatisticRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    private final FredbetProperties fredbetProperties;

    public StatisticRepository(NamedParameterJdbcOperations namedParameterJdbcOperations, FredbetProperties fredbetProperties) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.fredbetProperties = fredbetProperties;
    }

    public List<Statistic> createStatistic() {
        final String query = """
                Select b.user_name, a.match_group, sum(b.points)
                from matches a join bet b on a.match_id = b.match_id
                where a.goals_team_one is not null
                and a.goals_team_two is not null
                and b.user_name not like :username
                group by b.user_name, a.match_group
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", fredbetProperties.adminUsername());

        final StatisticsCollector statisticsCollector = new StatisticsCollector();

        namedParameterJdbcOperations.query(query, params, (ResultSet rs) -> {
            String userName = rs.getString(1);
            String group = rs.getString(2);
            int points = rs.getInt(3);
            statisticsCollector.addValue(userName, group, points);
        });

        return statisticsCollector.getResult();
    }

    public Map<String, Integer> sumPointsPerUserForFavoriteCountry(Country favoriteCountry) {
        final String query = """
                Select a.user_name, sum(a.points)
                from bet a join matches b on a.match_id = b.match_id
                where b.country_one = :country or b.country_two = :country
                group by a.user_name
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("country", favoriteCountry.name());

        Map<String, Integer> userPoints = new HashMap<>();
        namedParameterJdbcOperations.query(query, params, (ResultSet rs) -> {
            String userName = rs.getString(1);
            int points = rs.getInt(2);
            if (!fredbetProperties.adminUsername().equals(userName)) {
                userPoints.put(userName, points);
            }
        });
        return userPoints;
    }

    private static final class StatisticsCollector {

        private final TreeMap<String, Statistic> statisticsMap = new TreeMap<>();

        public void addValue(String username, String group, Integer points) {
            Statistic statistic = statisticsMap.get(username);
            if (statistic == null) {
                statistic = new Statistic(username);
            }
            if (group.startsWith("GROUP")) {
                statistic.setPointsGroup(statistic.getPointsGroup() + points);
            } else if (Group.ROUND_OF_SIXTEEN.getName().equals(group)) {
                statistic.setPointsRoundOfSixteen(points);
            } else if (Group.QUARTER_FINAL.getName().equals(group)) {
                statistic.setPointsQuarterFinal(points);
            } else if (Group.SEMI_FINAL.getName().equals(group)) {
                statistic.setPointsSemiFinal(points);
            } else if (Group.FINAL.getName().equals(group)) {
                statistic.setPointsFinal(points);
            } else if (Group.GAME_FOR_THIRD.getName().equals(group)) {
                statistic.setPointsGameForThird(points);
            }
            statisticsMap.put(username, statistic);
        }

        public List<Statistic> getResult() {
            return new ArrayList<>(statisticsMap.values());
        }
    }

}
