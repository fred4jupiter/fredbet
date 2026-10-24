package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntryType;

/**
 * Club teams, e.g. of the Club World Cup.
 */
public enum ClubTeam implements TeamBundleEntry {

    AL_AHLY("al-ahly"),

    ATLETICO_MADRID("atletico-madrid"),

    BOTAFOGO("botafogo"),

    FC_PORTO("fc-porto"),

    INTER_MIAMI("inter-miami-cf"),

    MANCHESTER_CITY("manchester-city"),

    PALMEIRAS("palmeiras"),

    PARIS_SAINT_GERMAIN("paris-saint-germain"),

    SEATLE_SOUNDERS("seattle-sounders"),

    AL_AIN_FC("al-ain-fc"),

    AL_HILAL("al-hilal"),

    AUCKLAND_CITY("auckland-city"),

    BAYERN_MUENCHEN("bayern-muenchen"),

    BENFICA_LISSABON("benfica-lissabon"),

    BOCA_JUNIORS("boca-juniors"),

    BORUSSIA_DORTMUND("borussia-dortmund"),

    CA_RIVER_PLATE_MONTEVIDEO("ca-river-plate-montevideo"),

    CF_MONTERREY("cf-monterrey"),

    CF_PACHUCA("cf-pachuca"),

    CHELSEA_CREST("chelsea-crest"),

    CLUB_LEON("club-leon"),

    CR_FLAMENGO("cr-flamengo"),

    ESPERANCE_SPORTIVE_TUNIS("esperance-sportive-tunis"),

    FC_INTERNAZIONALE_MILANO("fc-internazionale-milano"),

    FC_SALZBURG("fc-salzburg"),

    FLUMINENSE_FC("fluminense-fc"),

    JUVENTUS_FC("juventus-fc"),

    MAMELODI_SUNDOWNS_FC("mamelodi-sundowns-fc"),

    REAL_MADRID("real-madrid"),

    ULSAN_HD("ulsan-hd"),

    URAWA_RED_DIAMONDS("urawa-red-diamonds"),

    WYDAD_CASABLANCA("wydad-casablanca");

    // file name (without extension) of the crest in static/club-wm-icons/flags
    private final String crestFileName;

    ClubTeam(String crestFileName) {
        this.crestFileName = crestFileName;
    }

    public String getCrestFileName() {
        return crestFileName;
    }

    @Override
    public TeamBundleEntryType getType() {
        return TeamBundleEntryType.CLUB;
    }

    @Override
    public String getMessageKey() {
        return "team." + name();
    }

    public static ClubTeam fromName(String name) {
        for (ClubTeam clubTeam : values()) {
            if (clubTeam.name().equals(name)) {
                return clubTeam;
            }
        }

        return null;
    }
}
