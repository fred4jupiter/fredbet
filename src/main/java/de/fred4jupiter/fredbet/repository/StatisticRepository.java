package de.fred4jupiter.fredbet.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Statistic;
import de.fred4jupiter.fredbet.props.FredbetConstants;

@Repository
public class StatisticRepository {

	@Autowired
	private NamedParameterJdbcOperations namedParameterJdbcOperations;

	public List<Statistic> createStatistic() {
		StringBuilder builder = new StringBuilder();
		builder.append("Select b.user_name, a.match_group, sum(b.points) ");
		builder.append("from matches a join bet b on a.match_id = b.match_id ");
		builder.append("where a.goals_team_one is not null  ");
		builder.append("and a.goals_team_two is not null  ");
		builder.append("and b.user_name not like :username ");
		builder.append("group by b.user_name, a.match_group;");

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", FredbetConstants.TECHNICAL_USERNAME);

		final StatisticsCollector statisticsCollector = new StatisticsCollector();

		namedParameterJdbcOperations.query(builder.toString(), params, (ResultSet rs) -> {
			String userName = rs.getString(1);
			String group = rs.getString(2);
			int points = rs.getInt(3);
			statisticsCollector.addValue(userName, group, points);
		});

		return statisticsCollector.getResult();
	}

	public Map<String, Integer> sumPointsPerUserForFavoriteCountry(Country favoriteCountry) {
		StringBuilder builder = new StringBuilder();
		builder.append("Select a.user_name, sum(a.points) ");
		builder.append("from bet a join matches b on a.match_id = b.match_id ");
		builder.append("where b.country_one = :countryId or b.country_two = :countryId  ");
		builder.append("group by a.user_name ");

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("countryId", favoriteCountry.ordinal());

		Map<String, Integer> userPoints = new HashMap<>();
		namedParameterJdbcOperations.query(builder.toString(), params, (ResultSet rs) -> {
			String userName = rs.getString(1);
			int points = rs.getInt(2);
			if (!FredbetConstants.TECHNICAL_USERNAME.equals(userName)) {
				userPoints.put(userName, points);
			}
		});
		return userPoints;
	}

	private static final class StatisticsCollector {

		private TreeMap<String, Statistic> statisticsMap = new TreeMap<>();

		public void addValue(String username, String group, Integer points) {
			Statistic statistic = statisticsMap.get(username);
			if (statistic == null) {
				statistic = new Statistic(username);
			}
			if (group.startsWith("GROUP")) {
				statistic.setPointsGroup(statistic.getPointsGroup().intValue() + points.intValue());
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
			return new ArrayList<Statistic>(statisticsMap.values());
		}
	}

}
