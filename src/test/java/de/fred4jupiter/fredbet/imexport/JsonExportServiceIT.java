package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.util.TempFileWriterUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class JsonExportServiceIT {

    @Autowired
    private JsonExportService jsonExportService;

    @Autowired
    private JsonImportService jsonImportService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchService matchService;

    @Test
    public void exportAllAsJsonAndImportAgain() {
        final Match match = new Match();
        match.setGroup(Group.GROUP_A);
        match.setStadium("Munich");
        match.setKickOffDate(LocalDateTime.now());

        final Country teamOneCountry = Country.GERMANY;
        Team teamOne = new Team();
        teamOne.setCountry(teamOneCountry);
        match.setGoalsTeamOne(1);
        match.setTeamOne(teamOne);

        Team teamTwo = new Team();
        teamTwo.setCountry(Country.ALBANIA);
        match.setGoalsTeamTwo(2);
        match.setTeamTwo(teamTwo);

        Match savedMatch = matchService.save(match);
        assertThat(savedMatch).isNotNull();

        String json = jsonExportService.exportAllToJson(false);
        boolean result = TempFileWriterUtil.writeToTempFolder(json.getBytes(), "fredbet_export.json");
        assertThat(result).isTrue();

        jsonImportService.importAllFromJson(json);

        Optional<Match> foundOpt = matchRepository.findByBusinessKey(savedMatch.getBusinessKey());
        assertThat(foundOpt.isPresent()).isTrue();

        Match foundMatch = foundOpt.get();
        assertThat(foundMatch.getTeamOne().getCountry()).isEqualTo(teamOneCountry);
        assertThat(foundMatch.getTeamTwo().getCountry()).isEqualTo(teamTwo.getCountry());
        assertThat(foundMatch.getGoalsTeamOne()).isEqualTo(savedMatch.getGoalsTeamOne());
        assertThat(foundMatch.getGoalsTeamTwo()).isEqualTo(savedMatch.getGoalsTeamTwo());
        assertThat(foundMatch.getGroup()).isEqualTo(savedMatch.getGroup());
        assertThat(foundMatch.getKickOffDate()).isNotNull();
        assertThat(foundMatch.getStadium()).isEqualTo(savedMatch.getStadium());
    }
}
