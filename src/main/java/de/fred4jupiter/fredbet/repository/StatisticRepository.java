package de.fred4jupiter.fredbet.repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import de.fred4jupiter.fredbet.FredbetConstants;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Statistic;

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
			statisticsCollector.addValue(rs.getString(1), rs.getString(2), rs.getInt(3));
		});

		return statisticsCollector.getResult();
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
			}
			statisticsMap.put(username, statistic);
		}

		public List<Statistic> getResult() {
			return new ArrayList<Statistic>(statisticsMap.values());
		}
	}
}
