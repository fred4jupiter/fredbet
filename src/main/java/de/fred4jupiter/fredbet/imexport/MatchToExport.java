package de.fred4jupiter.fredbet.imexport;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Team;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

record MatchToExport(Team teamOne, Team teamTwo, Group group, boolean penaltyWinnerOne,
                     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm") LocalDateTime kickOffDate,
                     String stadium,
                     String matchBusinessKey,
                     Integer goalsTeamOne,
                     Integer goalsTeamTwo,
                     String externalId,
                     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm Z") ZonedDateTime externalLastUpdated) {

}
