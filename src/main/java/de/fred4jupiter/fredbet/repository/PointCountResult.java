package de.fred4jupiter.fredbet.repository;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PointCountResult {

	private final String username;

	private final Integer points;

	private final Long numberOfPointsCount;

	public PointCountResult(String username, Integer points, Long numberOfPointsCount) {
		this.username = username;
		this.points = points;
		this.numberOfPointsCount = numberOfPointsCount;
	}

	public String getUsername() {
		return username;
	}

	public Integer getPoints() {
		return points;
	}

	public Long getNumberOfPointsCount() {
		return numberOfPointsCount;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("username", username);
		builder.append("points", points);
		builder.append("numberOfPointsCount", numberOfPointsCount);
		return builder.toString();
	}
}
