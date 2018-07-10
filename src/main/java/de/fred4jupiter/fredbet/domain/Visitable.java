package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.service.ranking.Visitor;

public interface Visitable {

    void accept(Visitor visitor);
}
