package de.fred4jupiter.fredbet.domain;

/**
 * Country ISO Code 3166 ALPHA-3
 * 
 * @author michael
 *
 */
public enum Country {

	NONE("none"), // non selected country placeholder

	ALBANIA("alb"), BELGIUM("bel"), GERMANY("deu"), ENGLAND("eng"), FRANCE("fra"), IRELAND("irl"), ICELAND("isl"), ITALY("ita"),

	CROATIA("hrv"), NORTH_IRELAND("north_ireland"), AUSTRIA("aut"), POLAND("pol"), PORTUGAL("prt"), ROMANIA("rou"), RUSSIA("rus"),

	SWEDEN("swe"), SWITZERLAND("che"), SLOVAKIA("svk"), SPAIN("esp"), CZECH_REPUBLIC("cze"), TURKEY("tur"), UKRAINE("ukr"),

	HUNGARY("hun"), WALES("wales"), AUSTRALIA("aus"), EGYPT("egy"), ALGERIA("alg"), ANGOLA("ang"), EQUATORIAL_GUINEA("eqg"),

	ETHIOPIA("eth"), BENIN("ben"), BURKINA_FASO("bfa"), BURUNDI("bdi"), DJIBOUTI("dji"), IVORY_COAST("civ"),

	ERITREA("eri"), GABON("gab"), GAMBIA("gam"), GHANA("gha"), GUINEA("gui"), GUINEA_BISSEAU("gnb"), BOTSWANA("bot"),

	CAMEROON("cmr"), CAPE_VERDE_ISLANDS("cpv"), CENTRAL_AFRICAN_REPUBLIC("cta"), CHAD("cha"), COMOROS("com"), CONGO("cgo"),

	CONGO_DR("cod"), GUINEA_BISSAU("gnb"), KENYA("ken"), LESOTHO("les"), LIBERIA("lbr"), LIBYA("lby"), MADAGASCAR("mad"),

	MALAWI("mwi"), MALI("mli"), MAURITANIA("mtn"), MAURITIUS("mus"),

	MOROCCO("mar"), MOZAMBIQUE("moz"), NAMIBIA("nam"), NIGER("nig"), NIGERIA("nga"), RWANDA("rwa"), SAO_TOME_E_PRINCIPE("stp"),

	SENEGAL("sen"), SEYCHELLES("sey"), SIERRA_LEONE("sle"), SOMALIA("som"), SOUTH_AFRICA("rsa"), SOUTH_SUDAN("ssd"), SUDAN("sdn"),

	SWAZILAND("swz"), TANZANIA("tan"),

	TOGO("tog"), TUNISIA("tun"), UGANDA("uga"), ZAMBIA("zam"), AFGHANISTAN("afg"), BAHRAIN("bhr"), BANGLADESH("ban"), BHUTAN("bhu"),

	BRUNEI_DARUSSALAM("bru"), CAMBODIA("cam"), CHINA_PR("chn"), CHINESE_TAIPEI("tpe"), GUAM("gum"), HONG_KONG("hkg"), INDIA("ind"),

	IRAN("irn"), IRAQ("irq"), JAPAN("jpn"), JORDAN("jor"), KOREA_DPR("prk"), KOREA_REPUBLIC("kor"), KUWAIT("kuw"),

	KYRGYZSTAN("kgz"), LAOS("lao"), LEBANON("lib"), MACAU("mac"), MALAYSIA("mas"), MALDIVES("mdv"), MONGOLIA("mng"), MYANMAR("mya"),

	NEPAL("nep"), OMAN("oma"), PAKISTAN("pak"), PALESTINE("ple"), PHILIPPINES("phi"), QATAR("qat"), SAUDI_ARABIA("ksa"),

	SINGAPORE("sin"), SRI_LANKA("sri"), SYRIA("syr"), TAJIKISTAN("tjk"), THAILAND("tha"),

	TIMOR_LESTE("tls"), TURKMENISTAN("tkm"), UNITED_ARAB_EMIRATES("uae"), UZBEKISTAN("uzb"), VIETNAM("vie"), YEMEN("yem"), ANDORRA("and"),

	ARMENIA("arm"), AZERBAIJAN("aze"), BELARUS("blr"), BOSNIA_HERZEGOVINA("bih"), BULGARIA("bul"), CYPRUS("cyp"), DENMARK("den"), ESTONIA(
			"est"),

	FAROE_ISLANDS("fro"), FINLAND("fin"), FYR_MACEDONIA("mkd"), GEORGIA("geo"), GIBRALTAR("gib"), GREECE("gre"), ISRAEL("isr"), KAZAKHSTAN(
			"kaz"),

	KOSOVO("kvx"), LATVIA("lva"), LIECHTENSTEIN("lie"), LITHUANIA("ltu"), LUXEMBOURG("lux"), MALTA("mlt"), MOLDOVA("mda"), MONTENEGRO(
			"mne"),

	NETHERLANDS("ned"), NORWAY("nor"), SAN_MARINO("smr"), SCOTLAND("sco"), SERBIA("srb"), SLOVENIA("svk"),

	ANGUILLA("aia"), ANTIGUA_AND_BARBUDA("atg"), ARUBA("aru"), BAHAMAS("bah"), BARBADOS("brb"), BELIZE("blz"), BERMUDA("ber"),

	BRITISH_VIRGIN_ISLANDS("vgb"), CANADA("can"), CAYMAN_ISLANDS("cay"), COSTA_RICA("crc"), CUBA("cub"), CURACAO("cuw"), DOMINICA("dma"),

	DOMINICAN_REPUBLIC("dom"), EL_SALVADOR("slv"), GRENADA("grn"), GUATEMALA("gua"), GUYANA("guy"), HAITI("hai"), HONDURAS("hon"), JAMAICA(
			"jam"),

	MEXICO("mex"), MONTSERRAT("msr"), NICARAGUA("nca"), PANAMA("pan"), PUERTO_RICO("pur"), ST_KITTS_AND_NEVIS("skn"), ST_LUCIA("lca"),

	ST_VINCENT_GRENADINES("vin"), SURINAME("sur"), TRINIDAD_AND_TOBAGO("tri"), TURKS_AND_CAICOS_ISLANDS("tca"), US_VIRGIN_ISLANDS("vir"),

	USA("usa"), AMERICAN_SAMOA("asa"), COOK_ISLANDS("cok"), FIJI("fij"), NEW_CALEDONIA("ncl"), NEW_ZEALAND("nzl"), PAPUA_NEW_GUINEA("png"),

	SAMOA("sam"), SOLOMON_ISLANDS("sol"), TAHITI("tah"), TONGA("tga"), VANUATU("van"), ARGENTINA("arg"), BOLIVIA("bol"), BRAZIL("bra"),

	CHILE("chi"), COLOMBIA("col"), ECUADOR("ecu"), PARAGUAY("par"), PERU("per"), URUGUAY("uru"), VENEZUELA("ven");

	public static final String ICON_BASE_PATH = "/images/flags/";

	private static final String ICON_BASE_PATH_BIG = "/images/flags/42_28/";

	private String isoCode;

	private Country(String isoCode) {
		this.isoCode = isoCode;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public static Country fromName(String name) {
		for (Country country : values()) {
			if (country.name().equals(name)) {
				return country;
			}
		}

		return null;
	}

	public static Country fromIsoCode(String isoCountryCode) {
		for (Country country : values()) {
			if (country.getIsoCode().equalsIgnoreCase(isoCountryCode)) {
				return country;
			}
		}

		return null;
	}

	public String getIconPath() {
		return ICON_BASE_PATH + isoCode + ".png";
	}

	public String getIconPathBig() {
		return ICON_BASE_PATH_BIG + isoCode + ".png";
	}
}
