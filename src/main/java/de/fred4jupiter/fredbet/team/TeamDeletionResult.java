package de.fred4jupiter.fredbet.team;

/**
 * Number of deleted teams and of teams that were kept because they are still used in a match or an extra bet.
 */
public record TeamDeletionResult(int deleted, int kept) {
}
