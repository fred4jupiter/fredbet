package de.fred4jupiter.fredbet.ranking;

import de.fred4jupiter.fredbet.domain.entity.Bet;

public interface Visitor {

    void visit(Bet bet);
}
