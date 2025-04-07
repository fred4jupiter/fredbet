package de.fred4jupiter.fredbet.imexport;

record BetToExport(String username,
                   String matchBusinessKey,
                   Integer goalsTeamOne,
                   Integer goalsTeamTwo,
                   Integer points,
                   boolean penaltyWinnerOne,
                   boolean joker) {


}
