package de.fred4jupiter.fredbet.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Group {

	GROUP_A("group.title.A"),

	GROUP_B("group.title.B"),

	GROUP_C("group.title.C"),

	GROUP_D("group.title.D"),

	GROUP_E("group.title.E"),

	GROUP_F("group.title.F"),
	
	GROUP_G("group.title.G"),
	
	GROUP_H("group.title.H"),

	ROUND_OF_SIXTEEN("group.title.roundOfSixteen"), // Achtelfinale

	QUARTER_FINAL("group.title.quarterFinal"), // Viertelfinale

	SEMI_FINAL("group.title.semiFinal"), // Halbfinale

	FINAL("group.title.final"), // Finale
    
    GAME_FOR_THIRD("group.title.gameForThird"); // Finale

	private String titleMsgKey;

	private Group(String titleMsgKey) {
		this.titleMsgKey = titleMsgKey;
	}

	public String getTitleMsgKey() {
		return titleMsgKey;
	}

	public String getName() {
		return this.name();
	}

	public static List<Group> getAllGroups() {
		return Arrays.asList(Group.values());
	}

	public static List<Group> getMainGroups() {
		List<Group> groups = Arrays.asList(values());
		return groups.stream().filter(group -> group.name().startsWith("GROUP_")).collect(Collectors.toList());
	}
	
	public static List<Group> getFinalGroups() {
        return Arrays.asList(ROUND_OF_SIXTEEN, QUARTER_FINAL, SEMI_FINAL, FINAL);
    }
}
