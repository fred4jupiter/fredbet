package de.fred4jupiter.fredbet.domain;

public enum RankingSelection {

	MIXED("mixed"),

	ONLY_CHILDREN("child"),

	ONLY_ADULTS("adult");

	private String mode;

	private RankingSelection(String mode) {
		this.mode = mode;
	}

	public String getMode() {
		return mode;
	}

	public static RankingSelection fromMode(String mode) {
		for (RankingSelection rankingSelection : values()) {
			if (rankingSelection.getMode().equals(mode)) {
				return rankingSelection;
			}
		}
		
		throw new IllegalArgumentException("Unsupported mode "+mode);
	}
}
