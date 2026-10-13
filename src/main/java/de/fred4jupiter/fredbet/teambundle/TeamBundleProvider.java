package de.fred4jupiter.fredbet.teambundle;

import de.fred4jupiter.fredbet.domain.ClubTeam;
import de.fred4jupiter.fredbet.domain.Country;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.fred4jupiter.fredbet.domain.Country.*;

@Component
public class TeamBundleProvider {

    public List<TeamBundleEntry> getTeams(TeamBundle teamBundle) {
        return switch (teamBundle) {
            case WORLD_CUP -> List.of(Country.values());
            case CLUB_WM -> List.of(ClubTeam.values());
            case EUROPEAN_CHAMPIONSHIP -> getTeamsForEuropeanChampionship();
        };
    }

    private List<TeamBundleEntry> getTeamsForEuropeanChampionship() {
        return List.of(
            ALBANIA,
            BELGIUM,
            GERMANY,
            GREAT_BRITAIN,
            ENGLAND,
            FRANCE,
            IRELAND,
            ICELAND,
            ITALY,
            CROATIA,
            NORTH_IRELAND,
            AUSTRIA,
            POLAND,
            PORTUGAL,
            ROMANIA,
            SWEDEN,
            SWITZERLAND,
            SLOVAKIA,
            SPAIN,
            CZECH_REPUBLIC,
            TURKEY,
            UKRAINE,
            HUNGARY,
            WALES,
            BULGARIA,
            CYPRUS,
            FINLAND,
            GREECE,
            LUXEMBOURG,
            MONTENEGRO,
            NETHERLANDS,
            NORWAY,
            SCOTLAND,
            SERBIA,
            SLOVENIA
        );
    }
}
