package de.fred4jupiter.fredbet.imexport;

public interface MatchBusinessKey {

    /**
     * Returns the business key for a match. The key is constructed by a hash key with team members and match date.
     *
     * @return the match business key.
     */
    Integer getMatchBusinessKey();
}
