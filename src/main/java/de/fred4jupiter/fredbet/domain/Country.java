package de.fred4jupiter.fredbet.domain;

public enum Country {

	ALBANIA("al"),

	BELGIUM("be"),

	GERMANY("de"),

	ENGLAND("en"),

	FRANCE("fr"),

	IRELAND("ie"),

	ICELAND("is"),

	ITALY("it"),

	CROATIA("hr"),

	NORTH_IRLAND("north_irland"),

	AUSTRIA("at"),

	POLAND("pl"),

	PORTUGAL("pt"),

	ROMANIA("ro"),

	RUSSIA("ru"),

	SWEDEN("se"),

	SWITZERLAND("ch"),

	SLOVAKIA("sk"),

	SPAIN("es"),

	CZECH_REPUBLIC("cz"),

	TURKEY("tr"),

	UKRAINE("ua"),

	HUNGARY("hu"),

	WALES("wales");

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

}
