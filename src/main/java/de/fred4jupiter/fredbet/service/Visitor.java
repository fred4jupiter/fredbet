package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.domain.Bet;

public interface Visitor {

    void visit(Bet bet);
}
