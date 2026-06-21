package de.fred4jupiter.fredbet.team;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.crests.CrestPlaceholderLoader;
import de.fred4jupiter.fredbet.crests.CrestsCountryResolver;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.integration.CrestsDownloader;
import de.fred4jupiter.fredbet.match.TeamRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        Team team = new Team();
        team.setCountry(Country.GERMANY);
        SvgImage expected = new SvgImage("<svg>germany</svg>", 12);

        when(teamRepository.findById(5L)).thenReturn(Optional.of(team));
        when(crestsCountryResolver.loadCrestsImageFor(Country.GERMANY)).thenReturn(expected);

        SvgImage result = teamService.loadCrestImage(5L);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void findOrCreateTeam_whenExistingTeamFound_returnsExistingWithoutSaving() {
        Team existing = new Team();
        existing.setName("Existing Team");

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
}

