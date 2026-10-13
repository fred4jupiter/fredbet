package de.fred4jupiter.fredbet.web.team;

import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.team.TeamService;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntryType;
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
        String typePart = parts.length > 1 ? parts[1] : null;
        String keyPart = parts.length > 2 ? parts[2] : null;
        String namePart = parts.length > 3 ? parts[3] : null;

        if (StringUtils.isNotBlank(idPart) && !"null".equals(idPart)) {
            Long teamId = Long.parseLong(idPart);
            return teamService.findTeamById(teamId);
        }

        TeamBundleEntry teamBundleEntry = parseTeamBundleEntry(typePart, keyPart);
        String teamName = parseTeamName(namePart);
        return teamService.findOrCreateTeam(teamBundleEntry, teamName);
    }

    private String parseTeamName(String namePart) {
        if (namePart == null || "null".equals(namePart)) {
            return null;
        }
        return namePart;
    }

    private TeamBundleEntry parseTeamBundleEntry(String typePart, String keyPart) {
        if (typePart == null || "null".equals(typePart) || keyPart == null || "null".equals(keyPart)) {
            return null;
        }
        return TeamBundleEntryType.valueOf(typePart).resolve(keyPart);
    }
}
