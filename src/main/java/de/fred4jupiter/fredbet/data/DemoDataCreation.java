package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.teambundle.TeamBundle;

public record DemoDataCreation(TeamBundle teamBundle, Integer numberOfGroups, boolean withBets, boolean withResults) {
}
