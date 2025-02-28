package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.util.TempFileWriterUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class JsonExportServiceIT {

    @Autowired
    private JsonExportService jsonExportService;

    @Autowired
    private JsonImportService jsonImportService;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    public void exportAllAsJsonAndImportAgain() {
        Match match = new Match();
        match.setGroup(Group.GROUP_A);
        match.setStadium("Munich");
        match.setKickOffDate(LocalDateTime.now());

        final Country teamOneCountry = Country.GERMANY;
        Team teamOne = new Team();
        teamOne.setCountry(teamOneCountry);
        teamOne.setGoals(1);
        match.setTeamOne(teamOne);

        Team teamTwo = new Team();
        teamTwo.setCountry(Country.ALBANIA);
        teamTwo.setGoals(2);
        match.setTeamTwo(teamTwo);

        matchRepository.save(match);

        String json = jsonExportService.exportAllToJson();
        boolean result = TempFileWriterUtil.writeToTempFolder(json.getBytes(), "fredbet_export.json");
        assertThat(result).isTrue();

        jsonImportService.importAllFromJson(json);

        List<Match> matches = matchRepository.findByTeamOneCountry(teamOneCountry);
        assertThat(matches).hasSizeGreaterThanOrEqualTo(1);
        assertThat(matches).extracting(Match::getTeamOne).contains(teamOne);
        assertThat(matches).extracting(Match::getTeamTwo).contains(teamTwo);
        assertThat(matches).extracting(Match::getGroup).contains(match.getGroup());
        assertThat(matches).extracting(Match::getKickOffDate).isNotNull();
        assertThat(matches).extracting(Match::getStadium).contains(match.getStadium());
    }
}
