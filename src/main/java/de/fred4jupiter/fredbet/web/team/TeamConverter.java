package de.fred4jupiter.fredbet.web.team;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.team.TeamService;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TeamConverter implements Converter<String, Team> {

    private final TeamService teamService;

    public TeamConverter(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public Team convert(@NonNull String uniqueId) {
        if (StringUtils.isBlank(uniqueId)) {
            return teamService.createFallbackPlaceholder();
        }

        String[] parts = uniqueId.split("@");
        String idPart = parts.length > 0 ? parts[0] : null;
        String countryPart = parts.length > 1 ? parts[1] : null;
        String namePart = parts.length > 2 ? parts[2] : null;

        if (StringUtils.isNotBlank(idPart) && !"null".equals(idPart)) {
            Long teamId = Long.parseLong(idPart);
            return teamService.findTeamById(teamId);
        }

        Country country = parseCountry(countryPart);
        String teamName = parseTeamName(namePart);
        return teamService.findOrCreateTeam(country, teamName);
    }

    private String parseTeamName(String namePart) {
        if (namePart == null || "null".equals(namePart)) {
            return null;
        }
        return namePart;
    }

    private Country parseCountry(String countryPart) {
        if (countryPart == null || "null".equals(countryPart)) {
            return null;
        }
        return Country.valueOf(countryPart);
    }
}
