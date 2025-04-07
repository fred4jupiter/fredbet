package de.fred4jupiter.fredbet.imexport;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Team;

import java.time.LocalDateTime;

record MatchToExport(Team teamOne, Team teamTwo, Group group, boolean penaltyWinnerOne,
                     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm") LocalDateTime kickOffDate,
                     String stadium,
                     String matchBusinessKey) {

}
