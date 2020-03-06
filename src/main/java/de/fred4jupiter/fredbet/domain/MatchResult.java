package de.fred4jupiter.fredbet.domain;

public interface MatchResult {

    boolean isTeamOneWinner();

    boolean isTeamTwoWinner();

    boolean isUndecidedResult();

    Group getGroup();

    Team getTeamOne();

    Team getTeamTwo();
}
