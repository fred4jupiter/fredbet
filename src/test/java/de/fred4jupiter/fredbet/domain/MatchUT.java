package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.common.UnitTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class MatchUT {

    @Test
    public void isTeamOneWinner() {
        Match match = MatchBuilder.create()
                .withTeams("a", "b")
                .withGoals(2, 1)
                .withGroup(Group.GROUP_A)
                .build();
        assertThat(match).isNotNull();
        assertThat(match.isGroupMatch()).isTrue();
        assertThat(match.isTeamOneWinner()).isTrue();
        assertThat(match.isTeamTwoWinner()).isFalse();
    }

    @Test
    public void isTeamTwoWinner() {
        Match match = MatchBuilder.create()
                .withTeams("a", "b")
                .withGoals(1, 2)
                .withGroup(Group.GROUP_A)
                .build();
        assertThat(match).isNotNull();
        assertThat(match.isGroupMatch()).isTrue();
        assertThat(match.isTeamOneWinner()).isFalse();
        assertThat(match.isTeamTwoWinner()).isTrue();
    }

    @Test
    public void undecidedResult() {
        Match match = MatchBuilder.create()
                .withTeams("a", "b")
                .withGoals(1, 1)
                .withGroup(Group.GROUP_A)
                .build();
        assertThat(match).isNotNull();
        assertThat(match.isGroupMatch()).isTrue();
        assertThat(match.isTeamOneWinner()).isFalse();
        assertThat(match.isTeamTwoWinner()).isFalse();
    }

    @Test
    public void undecidedResultInKnockoutMatchAndPenaltyWinnerIsOne() {
        Match match = MatchBuilder.create()
                .withTeams("a", "b")
                .withGoals(1, 1)
                .withPenaltyWinnerOne(true)
                .withGroup(Group.FINAL)
                .build();
        assertThat(match).isNotNull();
        assertThat(match.isGroupMatch()).isFalse();
        assertThat(match.isTeamOneWinner()).isTrue();
        assertThat(match.isTeamTwoWinner()).isFalse();
        assertThat(match.isPenaltyWinnerOne()).isTrue();
    }

    @Test
    public void undecidedResultInKnockoutMatchAndPenaltyWinnerIsTwo() {
        Match match = MatchBuilder.create()
                .withTeams("a", "b")
                .withGoals(1, 1)
                .withPenaltyWinnerOne(false)
                .withGroup(Group.FINAL)
                .build();
        assertThat(match).isNotNull();
        assertThat(match.isGroupMatch()).isFalse();
        assertThat(match.isTeamOneWinner()).isFalse();
        assertThat(match.isTeamTwoWinner()).isTrue();
        assertThat(match.isPenaltyWinnerOne()).isFalse();
    }
}
