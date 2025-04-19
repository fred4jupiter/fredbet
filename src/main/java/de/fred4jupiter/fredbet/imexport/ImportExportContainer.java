package de.fred4jupiter.fredbet.imexport;

import java.util.List;

record ImportExportContainer(List<UserToExport> users,
                             List<MatchToExport> matches,
                             List<BetToExport> bets,
                             List<ExtraBetToExport> extraBets) {

}
