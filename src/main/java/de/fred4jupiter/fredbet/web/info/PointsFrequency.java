package de.fred4jupiter.fredbet.web.info;

import java.util.List;

import de.fred4jupiter.fredbet.repository.PointCountResult;

public class PointsFrequency {

	private final Integer points;

	private final List<PointCountResult> list;

	public PointsFrequency(Integer points, List<PointCountResult> list) {
		super();
		this.points = points;
		this.list = list;
	}

	public Integer getPoints() {
		return points;
	}

	public List<PointCountResult> getList() {
		return list;
	}
}
