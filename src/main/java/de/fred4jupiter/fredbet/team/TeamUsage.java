package de.fred4jupiter.fredbet.team;

/**
 * Number of matches and extra bets a team is used in.
 */
public record TeamUsage(long matches, long extraBets) {

    public boolean isUsed() {
        return matches > 0 || extraBets > 0;
    }
}
