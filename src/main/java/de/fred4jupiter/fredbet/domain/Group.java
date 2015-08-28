package de.fred4jupiter.fredbet.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Group {

	GROUP_A,
	
	GROUP_B,
	
	GROUP_C,
	
	GROUP_D,
	
	GROUP_E,
	
	GROUP_F,
	
	GROUP_G,
	
	GROUP_H,
	
	GROUP_I,
	
	ROUND_OF_SIXTEEN, // Achtelfinale
	
	QUARTER_FINAL,
	
	SEMI_FINAL,
	
	FINAL;

	public String getName() {
		return this.name();
	}
	
	public static List<Group> getMainGroups() {
		List<Group> groups = Arrays.asList(values());
		return groups.stream().filter(group -> group.name().startsWith("GROUP_")).collect(Collectors.toList());
	}
}
