package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.user.UserService;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import de.fred4jupiter.fredbet.util.TempFileWriterUtil;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class JsonExportServiceUT {

    @InjectMocks
    private JsonExportService jsonExportService;

    @InjectMocks
    private JsonImportService jsonImportService;

    @Spy
    private JsonObjectConverter jsonObjectConverter = new JsonObjectConverter();

    @Mock
    private MatchService matchService;

    @Mock
    private BettingService bettingService;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<Match> matchCaptor;

    @Test
    public void exportAllAsJsonAndImportAgain() {
        Match match = new Match();
        match.setGroup(Group.GROUP_A);
        match.setStadium("Munich");
        match.setKickOffDate(LocalDateTime.now());

        Team teamOne = new Team();
        teamOne.setCountry(Country.GERMANY);
        teamOne.setGoals(1);
        match.setTeamOne(teamOne);

        Team teamTwo = new Team();
        teamTwo.setCountry(Country.GERMANY);
        teamTwo.setGoals(2);
        match.setTeamTwo(teamTwo);

        when(matchService.findAll()).thenReturn(List.of(match));

        String json = jsonExportService.exportAllToJson();
        boolean result = TempFileWriterUtil.writeToTempFolder(json.getBytes(), "fredbet_export.json");
        assertThat(result).isTrue();

        jsonImportService.importAllFromJson(json);

        verify(matchService).save(matchCaptor.capture());

        Match savedMatch = matchCaptor.getValue();
        assertThat(match.getId()).isEqualTo(savedMatch.getId());
        assertThat(match.getTeamOne()).isEqualTo(savedMatch.getTeamOne());
        assertThat(match.getTeamTwo()).isEqualTo(savedMatch.getTeamTwo());
        assertThat(match.getGroup()).isEqualTo(savedMatch.getGroup());
        assertThat(match.getKickOffDateFormated()).isEqualTo(savedMatch.getKickOffDateFormated());
        assertThat(match.getStadium()).isEqualTo(savedMatch.getStadium());
    }
}
