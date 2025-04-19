package de.fred4jupiter.fredbet.teambundle;

import de.fred4jupiter.fredbet.domain.Country;
import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.List;

import static de.fred4jupiter.fredbet.domain.Country.*;

public enum TeamBundle {

    WORLD_CUP,

    CLUB_WM;

    public List<Country> getTeams() {
        return switch (this) {
            case WORLD_CUP -> getTeamsForWorldCup();
            case CLUB_WM -> getTeamsForClubWm();
        };
    }

    private List<Country> getTeamsForWorldCup() {
        List<Country> all = Arrays.stream(Country.values()).toList();
        return ListUtils.subtract(all, getTeamsForClubWm());
    }

    private List<Country> getTeamsForClubWm() {
        return List.of(
            AL_AHLY,
            ATLETICO_MADRID,
            BOTAFOGO,
            FC_PORTO,
            INTER_MIAMI,
            MANCHESTER_CITY,
            PALMEIRAS,
            PARIS_SAINT_GERMAIN,
            SEATLE_SOUNDERS,
            AL_AIN_FC,
            AL_HILAL,
            AUCKLAND_CITY,
            BAYERN_MUENCHEN,
            BENFICA_LISSABON,
            BOCA_JUNIORS,
            BORUSSIA_DORTMUND,
            CA_RIVER_PLATE_MONTEVIDEO,
            CF_MONTERREY,
            CF_PACHUCA,
            CHELSEA_CREST,
            CLUB_LEON,
            CR_FLAMENGO,
            ESPERANCE_SPORTIVE_TUNIS,
            FC_INTERNAZIONALE_MILANO,
            FC_SALZBURG,
            FLUMINENSE_FC,
            JUVENTUS_FC,
            MAMELODI_SUNDOWNS_FC,
            REAL_MADRID,
            ULSAN_HD,
            URAWA_RED_DIAMONDS,
            WYDAD_CASABLANCA
        );
    }

}
