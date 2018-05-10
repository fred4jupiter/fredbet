package de.fred4jupiter.fredbet.domain;

public class Joker {

	private final Integer numberOfJokersUsed;
	private final Integer max;

	public Joker(Integer numberOfJokersUsed, Integer max) {
		this.numberOfJokersUsed = numberOfJokersUsed;
		this.max = max;
	}

	public Integer getMax() {
		return max;
	}

	public Integer getNumberOfJokersUsed() {
		return numberOfJokersUsed;
	}
	
	public boolean isMaximumReached() {
		return numberOfJokersUsed.intValue() == max.intValue();
	}

}
