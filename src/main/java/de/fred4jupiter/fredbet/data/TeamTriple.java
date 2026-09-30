package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.domain.entity.Team;

public record TeamTriple(Team finalWinner, Team semiFinalWinner, Team thirdFinalWinner) {
}
