package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.teambundle.TeamKey;

/**
 * Country ISO Code 3166 ALPHA-3
 *
 * @author michael
 */
public enum Country implements TeamKey {

    ALBANIA("alb"), BELGIUM("bel"), GERMANY("deu"), GREAT_BRITAIN("gbr"), ENGLAND("eng", "gb-eng"), FRANCE("fra"),
    IRELAND("irl"), ICELAND("isl"), ITALY("ita"), CROATIA("hrv"),
    NORTH_IRELAND("north_ireland", "gb-nir"),
    AUSTRIA("aut"), POLAND("pol"), PORTUGAL("prt"), ROMANIA("rou"), RUSSIA("rus"),
    SWEDEN("swe"), SWITZERLAND("che"),
    SLOVAKIA("svk"), SPAIN("esp"), CZECH_REPUBLIC("cze"), TURKEY("tur"), UKRAINE("ukr"),

    HUNGARY("hun"), WALES("wales", "gb-wls"), AUSTRALIA("aus"), EGYPT("egy"), ALGERIA("dza"), ANGOLA("ago"), EQUATORIAL_GUINEA("gnq"),

    ETHIOPIA("eth"), BENIN("ben"), BURKINA_FASO("bfa"), BURUNDI("bdi"), DJIBOUTI("dji"), IVORY_COAST("civ"),

    ERITREA("eri"), GABON("gab"), GAMBIA("gmb"), GHANA("gha"), GUINEA("gin"), GUINEA_BISSEAU("gnb"), BOTSWANA("bwa"),

    CAMEROON("cmr"), CAPE_VERDE_ISLANDS("cpv"), CENTRAL_AFRICAN_REPUBLIC("caf"), CHAD("tcd"), COMOROS("com"), CONGO("cod"),

    CONGO_DR("cod"), KENYA("ken"), LESOTHO("lso"), LIBERIA("lbr"), LIBYA("lby"), MADAGASCAR("mdg"),

    MALAWI("mwi"), MALI("mli"), MAURITANIA("mrt"), MAURITIUS("mus"),

    MOROCCO("mar"), MOZAMBIQUE("moz"), NAMIBIA("nam"), NIGER("ner"), NIGERIA("nga"), RWANDA("rwa"), SAO_TOME_E_PRINCIPE("stp"),

    SENEGAL("sen"), SEYCHELLES("syc"), SIERRA_LEONE("sle"), SOMALIA("som"), SOUTH_AFRICA("zaf"), SOUTH_SUDAN("ssd"), SUDAN("sdn"),

    SWAZILAND("swz"), TANZANIA("tza"),

    TOGO("tgo"), TUNISIA("tun"), UGANDA("uga"), ZAMBIA("zmb"), AFGHANISTAN("afg"), BAHRAIN("bhr"), BANGLADESH("bgd"), BHUTAN("btn"),

    BRUNEI_DARUSSALAM("brn"), CAMBODIA("khm"), CHINA_PR("chn"), CHINESE_TAIPEI("twn"), GUAM("gum"), HONG_KONG("hkg"), INDIA("ind"),

    IRAN("irn"), IRAQ("irq"), JAPAN("jpn"), JORDAN("jor"), KOREA_DPR("prk"), KOREA_REPUBLIC("kor"), KUWAIT("kwt"),

    KYRGYZSTAN("kgz"), LAOS("lao"), LEBANON("lbn"), MACAU("mac"), MALAYSIA("mys"), MALDIVES("mdv"), MONGOLIA("mng"), MYANMAR("mmr"),

    NEPAL("npl"), OMAN("omn"), PAKISTAN("pak"), PALESTINE("pse"), PHILIPPINES("phl"), QATAR("qat"), SAUDI_ARABIA("sau"),

    SINGAPORE("sgp"), SRI_LANKA("lka"), SYRIA("syr"), TAJIKISTAN("tjk"), THAILAND("tha"),

    TIMOR_LESTE("tls"), TURKMENISTAN("tkm"), UNITED_ARAB_EMIRATES("are"), UZBEKISTAN("uzb"), VIETNAM("vnm"), YEMEN("yem"), ANDORRA("and"),

    ARMENIA("arm"), AZERBAIJAN("aze"), BELARUS("blr"), BOSNIA_HERZEGOVINA("bih"), BULGARIA("bgr"), CYPRUS("cyp"), DENMARK("dnk"), ESTONIA(
        "est"),

    FAROE_ISLANDS("fro"), FINLAND("fin"), MACEDONIA("mkd"), GEORGIA("geo"), GIBRALTAR("gib"), GREECE("grc"), ISRAEL("isr"), KAZAKHSTAN(
        "kaz"),

    LATVIA("lva"), LIECHTENSTEIN("lie"), LITHUANIA("ltu"), LUXEMBOURG("lux"), MALTA("mlt"), MOLDOVA("mda"), MONTENEGRO(
        "mne"),

    NETHERLANDS("nld"), NORWAY("nor"), SAN_MARINO("smr"), SCOTLAND("sco", "gb-sct"), SERBIA("srb"), SLOVENIA("svn"),

    ANGUILLA("aia"), ANTIGUA_AND_BARBUDA("atg"), ARUBA("abw"), BAHAMAS("bhs"), BARBADOS("brb"), BELIZE("blz"), BERMUDA("bmu"),

    BRITISH_VIRGIN_ISLANDS("vgb"), CANADA("can"), CAYMAN_ISLANDS("cym"), COSTA_RICA("cri"), CUBA("cub"), CURACAO("cuw"), DOMINICA("dma"),

    DOMINICAN_REPUBLIC("dom"), EL_SALVADOR("slv"), GRENADA("grd"), GUATEMALA("gtm"), GUYANA("guy"), HAITI("hti"), HONDURAS("hnd"), JAMAICA(
        "jam"),

    MEXICO("mex"), MONTSERRAT("msr"), NICARAGUA("nic"), PANAMA("pan"), PUERTO_RICO("pri"), ST_KITTS_AND_NEVIS("kna"), ST_LUCIA("lca"),

    ST_VINCENT_GRENADINES("vct"), SURINAME("sur"), TRINIDAD_AND_TOBAGO("tto"), TURKS_AND_CAICOS_ISLANDS("tca"), US_VIRGIN_ISLANDS("vir"),

    USA("usa"), COOK_ISLANDS("cok"), FIJI("fji"), NEW_CALEDONIA("ncl"), NEW_ZEALAND("nzl"), PAPUA_NEW_GUINEA("png"),

    SAMOA("wsm"), SOLOMON_ISLANDS("slb"), TAHITI("pyf"), TONGA("ton"), VANUATU("vut"), ARGENTINA("arg"), BOLIVIA("bol"), BRAZIL("bra"),

    CHILE("chl"), COLOMBIA("col"), ECUADOR("ecu"), PARAGUAY("pry"), PERU("per"), URUGUAY("ury"), VENEZUELA("ven"),

    // start of Club WM teams
    AL_AHLY("", "", "kwm kwm-al-ahly"),

    ATLETICO_MADRID("", "", "kwm kwm-atletico-madrid"),

    BOTAFOGO("", "", "kwm kwm-botafogo"),

    FC_PORTO("", "", "kwm kwm-fc-porto"),

    INTER_MIAMI("", "", "kwm kwm-inter-miami"),

    MANCHESTER_CITY("", "", "kwm kwm-manchester"),

    PALMEIRAS("", "", "kwm kwm-palmeiras"),

    PARIS_SAINT_GERMAIN("", "", "kwm kwm-paris-saint-germain"),

    SEATLE_SOUNDERS("", "", "kwm kwm-seattle-sounders"),

    AL_AIN_FC("", "", "kwm kwm-al-ain-fc"),

    AL_HILAL("", "", "kwm kwm-al-hilal"),

    AUCKLAND_CITY("", "", "kwm kwm-auckland-city"),

    BAYERN_MUENCHEN("", "", "kwm kwm-bayern-muenchen"),

    BENFICA_LISSABON("", "", "kwm kwm-benfica-lissabon"),

    BOCA_JUNIORS("", "", "kwm kwm-boca-juniors"),

    BORUSSIA_DORTMUND("", "", "kwm kwm-borussia-dortmund"),

    CA_RIVER_PLATE_MONTEVIDEO("", "", "kwm kwm-ca-river-plate-montevideo"),

    CF_MONTERREY("", "", "kwm kwm-cf-monterrey"),

    CF_PACHUCA("", "", "kwm kwm-cf-pachuca"),

    CHELSEA_CREST("", "", "kwm kwm-chelsea-crest"),

    CLUB_LEON("", "", "kwm kwm-club-leon"),

    CR_FLAMENGO("", "", "kwm kwm-cr-flamengo"),

    ESPERANCE_SPORTIVE_TUNIS("", "", "kwm kwm-esperance-sportive-tunis"),

    FC_INTERNAZIONALE_MILANO("", "", "kwm kwm-fc-internazionale-milano"),

    FC_SALZBURG("", "", "kwm kwm-fc-salzburg"),

    FLUMINENSE_FC("", "", "kwm kwm-fluminense-fc"),

    JUVENTUS_FC("", "", "kwm kwm-juventus-fc"),

    MAMELODI_SUNDOWNS_FC("", "", "kwm kwm-mamelodi-sundowns-fc"),

    REAL_MADRID("", "", "kwm kwm-real-madrid"),

    ULSAN_HD("", "", "kwm kwm-ulsan-hd"),

    URAWA_RED_DIAMONDS("", "", "kwm kwm-urawa-red-diamonds"),

    WYDAD_CASABLANCA("", "", "kwm kwm-wydad-casablanca");

    private final String alpha3Code;

    // special code for the flag in flag icons lib which differ from the ISO country code
    private final String flagIconCode;

    private final String cssIconClass;

    Country(String alpha3Code) {
        this.alpha3Code = alpha3Code;
        this.flagIconCode = null;
        this.cssIconClass = null;
    }

    Country(String alpha3Code, String flagIconCode) {
        this.alpha3Code = alpha3Code;
        this.flagIconCode = flagIconCode;
        this.cssIconClass = null;
    }

    Country(String alpha3Code, String flagIconCode, String cssIconClass) {
        this.alpha3Code = alpha3Code;
        this.flagIconCode = flagIconCode;
        this.cssIconClass = cssIconClass;
    }

    public String getAlpha3Code() {
        return alpha3Code;
    }

    public String getFlagIconCode() {
        return flagIconCode;
    }

    public String getCssIconClass() {
        return cssIconClass;
    }

    public static Country fromName(String name) {
        for (Country country : values()) {
            if (country.name().equals(name)) {
                return country;
            }
        }

        return null;
    }

}
