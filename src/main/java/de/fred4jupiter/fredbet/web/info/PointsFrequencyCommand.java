package de.fred4jupiter.fredbet.web.info;

import java.util.ArrayList;
import java.util.List;

import de.fred4jupiter.fredbet.repository.PointCountResult;

public class PointsFrequencyCommand {

	private List<PointsFrequency> resultList = new ArrayList<>();

	public List<PointsFrequency> getResultList() {
		return resultList;
	}

	public void add(Integer points, List<PointCountResult> list) {
		this.resultList.add(new PointsFrequency(points, list));		
	}
	
	public boolean isEmpty() {
		return this.resultList.isEmpty();
	}

}
