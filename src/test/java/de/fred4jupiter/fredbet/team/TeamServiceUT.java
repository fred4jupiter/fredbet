package de.fred4jupiter.fredbet.team;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.crests.CrestPlaceholderLoader;
import de.fred4jupiter.fredbet.crests.CrestsCountryResolver;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.integration.CrestsDownloader;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.match.TeamRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class TeamServiceUT {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private CrestsCountryResolver crestsCountryResolver;

    @Mock
    private CrestsDownloader crestsDownloader;

    @Mock
    private CrestPlaceholderLoader crestPlaceholderLoader;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private ExtraBetRepository extraBetRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    public void loadCrestImage_whenTeamDoesNotExist_returnsNull() {
        when(teamRepository.findById(7L)).thenReturn(Optional.empty());

        SvgImage crest = teamService.loadCrestImage(7L);

        assertThat(crest).isNull();
    }

    @Test
    public void loadCrestImage_whenCountryIsSet_usesCountryResolver() {
        Team team = new Team(Country.GERMANY);
        SvgImage expected = new SvgImage("<svg>germany</svg>", 12);

        when(teamRepository.findById(5L)).thenReturn(Optional.of(team));
        when(crestsCountryResolver.loadCrestsImageFor(Country.GERMANY)).thenReturn(expected);

        SvgImage result = teamService.loadCrestImage(5L);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void findOrCreateTeam_whenExistingTeamFound_returnsExistingWithoutSaving() {
        Team existing = new Team("Existing Team");

        when(teamRepository.findByCountryOrName(null, "Existing Team")).thenReturn(existing);

        Team result = teamService.findOrCreateTeam(null, "Existing Team");

        assertThat(result).isSameAs(existing);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    public void findOrCreateTeam_withoutCountryAndBlankName_usesFallbackNameAndPlaceholder() {
        SvgImage placeholder = new SvgImage("<svg>placeholder</svg>", 1);
        when(teamRepository.findByCountryOrName(null, TeamService.FALLBACK_TEAM_NAME)).thenReturn(null);
        when(crestPlaceholderLoader.getCrestPlaceholderIcon()).thenReturn(placeholder);
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Team result = teamService.findOrCreateTeam(null, " ");

        assertThat(result.getName()).isEqualTo(TeamService.FALLBACK_TEAM_NAME);
        assertThat(result.getSvgContent()).isEqualTo("<svg>placeholder</svg>");
    }

    @Test
    public void findOrCreateTeam_withFdTeamId_downloadsCrestWhenCountryCrestMissing() {
        when(teamRepository.findByCountryOrName(Country.GERMANY, TeamService.FALLBACK_TEAM_NAME)).thenReturn(null);
        when(crestsCountryResolver.loadCrestsImageFor(Country.GERMANY, false)).thenReturn(Optional.empty());
        when(crestsDownloader.downloadCrestsByUrl("fd-10")).thenReturn(new SvgImage("<svg>fd</svg>", 1));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Team result = teamService.findOrCreateTeam(Country.GERMANY, null, "fd-10");

        assertThat(result.getCountry()).isEqualTo(Country.GERMANY);
        assertThat(result.getSvgContent()).isEqualTo("<svg>fd</svg>");
    }

    @Test
    public void createTeam_withTeamNameAndCrest_savesUploadedCrest() {
        byte[] crest = "<svg xmlns=\"http://www.w3.org/2000/svg\"><circle r=\"5\"/></svg>".getBytes(StandardCharsets.UTF_8);
        when(teamRepository.findByName("FC Test")).thenReturn(null);
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Team result = teamService.createTeam(null, " FC Test ", crest);

        assertThat(result.getName()).isEqualTo("FC Test");
        assertThat(result.getCountry()).isNull();
        assertThat(result.getSvgContent()).isEqualTo(new String(crest, StandardCharsets.UTF_8));
    }

    @Test
    public void createTeam_withCountryWithoutCrest_usesCountryCrestAndIgnoresName() {
        when(teamRepository.findByCountry(Country.GERMANY)).thenReturn(null);
        when(crestsCountryResolver.loadCrestsImageFor(Country.GERMANY, false)).thenReturn(Optional.of(new SvgImage("<svg>germany</svg>")));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Team result = teamService.createTeam(Country.GERMANY, "ignored", null);

        assertThat(result.getCountry()).isEqualTo(Country.GERMANY);
        assertThat(result.getName()).isNull();
        assertThat(result.getSvgContent()).isEqualTo("<svg>germany</svg>");
    }

    @Test
    public void createTeam_whenTeamAlreadyExists_throwsException() {
        Team existing = new Team("FC Test");
        ReflectionTestUtils.setField(existing, "id", 3L);
        when(teamRepository.findByName("FC Test")).thenReturn(existing);

        assertThatThrownBy(() -> teamService.createTeam(null, "FC Test", null)).isInstanceOf(TeamAlreadyExistsException.class);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    public void createTeam_withInvalidCrest_throwsException() {
        byte[] crest = "<svg xmlns=\"http://www.w3.org/2000/svg\"><script>alert(1)</script></svg>".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> teamService.createTeam(null, "FC Test", crest)).isInstanceOf(InvalidCrestException.class);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    public void updateTeam_withoutNewCrestAndSameCountry_keepsStoredCrest() {
        Team team = new Team(Country.GERMANY);
        ReflectionTestUtils.setField(team, "id", 5L);
        team.setSvgContent(new SvgImage("<svg>custom</svg>"));
        when(teamRepository.findById(5L)).thenReturn(Optional.of(team));
        when(teamRepository.findByCountry(Country.GERMANY)).thenReturn(team);
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Team result = teamService.updateTeam(5L, Country.GERMANY, null, null);

        assertThat(result.getSvgContent()).isEqualTo("<svg>custom</svg>");
    }

    @Test
    public void updateTeam_fromCountryToTeamName_resetsCrestToPlaceholder() {
        Team team = new Team(Country.GERMANY);
        ReflectionTestUtils.setField(team, "id", 5L);
        team.setSvgContent(new SvgImage("<svg>germany</svg>"));
        when(teamRepository.findById(5L)).thenReturn(Optional.of(team));
        when(teamRepository.findByName("FC Test")).thenReturn(null);
        when(crestPlaceholderLoader.getCrestPlaceholderIcon()).thenReturn(new SvgImage("<svg>placeholder</svg>"));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Team result = teamService.updateTeam(5L, null, "FC Test", null);

        assertThat(result.getCountry()).isNull();
        assertThat(result.getName()).isEqualTo("FC Test");
        assertThat(result.getSvgContent()).isEqualTo("<svg>placeholder</svg>");
    }

    @Test
    public void deleteTeam_whenNotReferenced_deletesTeam() {
        Team team = new Team("FC Test");
        when(teamRepository.findById(5L)).thenReturn(Optional.of(team));
        when(matchRepository.existsByTeamOneOrTeamTwo(team, team)).thenReturn(false);
        when(extraBetRepository.existsByFinalWinnerOrSemiFinalWinnerOrThirdFinalWinner(team, team, team)).thenReturn(false);

        teamService.deleteTeam(5L);

        verify(teamRepository).delete(team);
    }

    @Test
    public void deleteTeam_whenUsedInMatch_throwsException() {
        Team team = new Team("FC Test");
        when(teamRepository.findById(5L)).thenReturn(Optional.of(team));
        when(matchRepository.existsByTeamOneOrTeamTwo(team, team)).thenReturn(true);

        assertThatThrownBy(() -> teamService.deleteTeam(5L)).isInstanceOf(TeamInUseException.class);
        verify(teamRepository, never()).delete(any(Team.class));
    }

    @Test
    public void deleteTeam_whenUsedInExtraBet_throwsException() {
        Team team = new Team("FC Test");
        when(teamRepository.findById(5L)).thenReturn(Optional.of(team));
        when(matchRepository.existsByTeamOneOrTeamTwo(team, team)).thenReturn(false);
        when(extraBetRepository.existsByFinalWinnerOrSemiFinalWinnerOrThirdFinalWinner(team, team, team)).thenReturn(true);

        assertThatThrownBy(() -> teamService.deleteTeam(5L)).isInstanceOf(TeamInUseException.class);
        verify(teamRepository, never()).delete(any(Team.class));
    }
}
