package de.fred4jupiter.fredbet.domain;

public enum Group {

	GROUP_A,
	
	GROUP_B,
	
	GROUP_C,
	
	GROUP_D,
	
	GROUP_E,
	
	GROUP_F,
	
	ROUND_OF_SIXTEEN, // Achtelfinale
	
	QUARTER_FINAL,
	
	SEMI_FINAL,
	
	FINAL;

	public String getName() {
		return this.name();
	}
}
