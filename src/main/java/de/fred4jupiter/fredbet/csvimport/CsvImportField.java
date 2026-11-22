package de.fred4jupiter.fredbet.csvimport;

import java.util.Arrays;
import java.util.List;

/**
 * The match fields a column of an uploaded CSV file can be mapped to.
 */
public enum CsvImportField {

    TEAM_ONE("csvimport.field.teamOne", true, "teamone", "team1", "hometeam", "home", "heim", "mannschaft1"),

    TEAM_TWO("csvimport.field.teamTwo", true, "teamtwo", "team2", "awayteam", "away", "gast", "auswaerts", "mannschaft2"),

    GROUP("csvimport.field.group", false, "group", "gruppe", "stage", "round", "runde"),

    KICK_OFF_DATE("csvimport.field.kickOffDate", true, "kickoffdate", "kickoff", "date", "datetime", "datum", "anstoss", "utcdate"),

    VENUE("csvimport.field.venue", false, "venue", "stadium", "location", "stadion", "ort", "spielort"),

    TEAM_ONE_IMAGE("csvimport.field.teamOneImage", false, "teamoneimage", "team1image", "teamonecrest", "team1crest", "hometeamcrest", "homecrest",
        "hometeamimage", "homeimage"),

    TEAM_TWO_IMAGE("csvimport.field.teamTwoImage", false, "teamtwoimage", "team2image", "teamtwocrest", "team2crest", "awayteamcrest", "awaycrest",
        "awayteamimage", "awayimage"),

    TEAM_ONE_SVG("csvimport.field.teamOneSvg", false, "teamonesvg", "team1svg", "teamonecrestsvg", "team1crestsvg", "hometeamsvg", "homesvg",
        "hometeamcrestsvg", "homecrestsvg"),

    TEAM_TWO_SVG("csvimport.field.teamTwoSvg", false, "teamtwosvg", "team2svg", "teamtwocrestsvg", "team2crestsvg", "awayteamsvg", "awaysvg",
        "awayteamcrestsvg", "awaycrestsvg");

    private final String msgKey;

    private final boolean required;

    // normalized column headers (lower case, only letters and digits) that are mapped to this field automatically
    private final List<String> headerAliases;

    CsvImportField(String msgKey, boolean required, String... headerAliases) {
        this.msgKey = msgKey;
        this.required = required;
        this.headerAliases = List.of(headerAliases);
    }

    public String getMsgKey() {
        return msgKey;
    }

    public boolean isRequired() {
        return required;
    }

    boolean matchesHeader(String normalizedHeader) {
        return headerAliases.contains(normalizedHeader);
    }

    public static List<CsvImportField> requiredFields() {
        return Arrays.stream(values()).filter(CsvImportField::isRequired).toList();
    }
}
