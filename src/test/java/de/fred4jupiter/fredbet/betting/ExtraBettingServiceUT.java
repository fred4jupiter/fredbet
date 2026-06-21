package de.fred4jupiter.fredbet.betting;

import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.data.RandomValueGenerator;
import de.fred4jupiter.fredbet.data.TeamTriple;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.props.IntegrationProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
public class ExtraBettingServiceUT {

    @Mock
    private ExtraBetRepository extraBetRepository;

    @Mock
    private MatchService matchService;

    @Mock
    private RandomValueGenerator randomValueGenerator;

    private final FredbetProperties fredbetProperties = new FredbetProperties("en", 1, 2, 3, "admin", "secret", null, null, new IntegrationProperties(null));

    @Test
    public void saveExtraBetDeletesExistingBetWhenNoSelectionsAreGiven() {
        ExtraBet existing = new ExtraBet();
        when(extraBetRepository.findByUserName("user")).thenReturn(existing);

        createService().saveExtraBet(null, null, null, "user");

        verify(extraBetRepository).delete(existing);
        verify(extraBetRepository, never()).save(existing);
    }

    @Test
    public void saveExtraBetStoresThirdPlaceWinnerOnlyWhenMatchExists() {
        when(matchService.isGameForThirdAvailable()).thenReturn(true);

        createService().saveExtraBet(Country.GERMANY, Country.FRANCE, Country.SPAIN, "user");

        verify(extraBetRepository).save(org.mockito.ArgumentMatchers.argThat(extraBet ->
            Country.SPAIN.equals(extraBet.getThirdFinalWinner()) && "user".equals(extraBet.getUserName())));
    }

    @Test
    public void loadExtraBetForUserCreatesEmptyBetWhenNoneExists() {
        when(extraBetRepository.findByUserName("new-user")).thenReturn(null);

        ExtraBet extraBet = createService().loadExtraBetForUser("new-user");

        assertThat(extraBet.getUserName()).isEqualTo("new-user");
    }

    @Test
    public void hasOpenExtraBetReturnsTrueWhenNoBetExists() {
        when(extraBetRepository.findByUserName("user")).thenReturn(null);

        assertThat(createService().hasOpenExtraBet("user")).isTrue();
    }

    @Test
    public void loadExtraBetDataOthersFiltersAdminAndSortsCaseInsensitive() {
        ExtraBet admin = createExtraBet("admin");
        ExtraBet zed = createExtraBet("zed");
        ExtraBet alfred = createExtraBet("Alfred");
        when(extraBetRepository.findAll(Sort.by(Sort.Direction.ASC, "userName"))).thenReturn(List.of(zed, admin, alfred));

        List<ExtraBet> result = createService().loadExtraBetDataOthers();

        assertThat(result).extracting(ExtraBet::getUserName).containsExactly("Alfred", "zed");
    }

    @Test
    public void createExtraBetForUserUsesRandomTripleWhenAvailable() {
        when(randomValueGenerator.generateTeamTriple()).thenReturn(new TeamTriple(Country.GERMANY, Country.FRANCE, Country.SPAIN));
        when(matchService.isGameForThirdAvailable()).thenReturn(true);

        createService().createExtraBetForUser("user");

        verify(extraBetRepository).save(org.mockito.ArgumentMatchers.argThat(extraBet ->
            Country.GERMANY.equals(extraBet.getFinalWinner()) && Country.FRANCE.equals(extraBet.getSemiFinalWinner())
                && Country.SPAIN.equals(extraBet.getThirdFinalWinner())));
    }

    private ExtraBettingService createService() {
        return new ExtraBettingService(extraBetRepository, matchService, fredbetProperties, randomValueGenerator);
    }

    private ExtraBet createExtraBet(String userName) {
        ExtraBet extraBet = new ExtraBet();
        extraBet.setUserName(userName);
        return extraBet;
    }
}

