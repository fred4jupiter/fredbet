package de.fred4jupiter.fredbet.integration.model;

public record FdCompetition(Integer id, FdArea area, String name, String code, String type, FdCompetitionSeason currentSeason) {
}
