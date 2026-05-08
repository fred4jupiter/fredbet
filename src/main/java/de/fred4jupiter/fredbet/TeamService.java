package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.crests.CrestPlaceholderLoader;
import de.fred4jupiter.fredbet.crests.CrestsCountryResolver;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.integration.CrestsDownloader;
import de.fred4jupiter.fredbet.match.TeamRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@Transactional
public class TeamService {

    private static final String FALLBACK_TEAM_NAME = "Not yet defined";

    private final TeamRepository teamRepository;

    private final CrestsCountryResolver crestsCountryResolver;

    private final CrestsDownloader crestsDownloader;

    private final CrestPlaceholderLoader crestPlaceholderLoader;

    public TeamService(TeamRepository teamRepository, CrestsCountryResolver crestsCountryResolver, CrestsDownloader crestsDownloader, CrestPlaceholderLoader crestPlaceholderLoader) {
        this.teamRepository = teamRepository;
        this.crestsCountryResolver = crestsCountryResolver;
        this.crestsDownloader = crestsDownloader;
        this.crestPlaceholderLoader = crestPlaceholderLoader;
    }

    public SvgImage loadCrestImage(Long teamId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            return null;
        }

        final Team team = teamOpt.get();

        if (team.getCountry() != null) {
            return crestsCountryResolver.loadCrestsImageFor(team.getCountry());
        }

        return new SvgImage(team.getSvgContent(), team.getVersion());
    }

    public Team findOrCreateTeam(Country country, String teamName) {
        return findOrCreateTeam(country, teamName, _ -> {
        });
    }

    public Team findOrCreateTeam(Country country, String teamName, String fdTeamId) {
        return findOrCreateTeam(country, teamName, newTeam -> {
            if (newTeam.getSvgContent() == null) {
                newTeam.setSvgContent(crestsDownloader.downloadCrestsByUrl(fdTeamId));
            }
        });
    }

    public Team findOrCreateTeam(Country country, String teamName, Consumer<Team> newTeamCallback) {
        Team team = teamRepository.findByCountryOrName(country, StringUtils.isNotBlank(teamName) ? teamName : FALLBACK_TEAM_NAME);
        if (team != null) {
            return team;
        }

        Team newTeam = new Team();
        if (country != null) {
            newTeam.setCountry(country);
            newTeam.setName(null);
            crestsCountryResolver.loadCrestsImageFor(country, false).ifPresent(newTeam::setSvgContent);
        } else {
            newTeam.setName(StringUtils.isNotBlank(teamName) ? teamName : FALLBACK_TEAM_NAME);
        }

        newTeamCallback.accept(newTeam);

        if (newTeam.getSvgContent() == null) {
            newTeam.setSvgContent(crestPlaceholderLoader.getCrestPlaceholderIcon());
        }

        return teamRepository.save(newTeam);
    }
}
