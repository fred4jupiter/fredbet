package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.domain.entity.Team;

public interface MatchResult {

    boolean isTeamOneWinner();

    boolean isTeamTwoWinner();

    boolean isUndecidedResult();

    Group getGroup();

    Team getTeamOne();

    Team getTeamTwo();
}
