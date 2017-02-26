package de.fred4jupiter.fredbet.domain;

public enum Country {

    NONE("none"), // non selected country placeholder

    ALBANIA("al"), BELGIUM("be"), GERMANY("de"), ENGLAND("en"), FRANCE("fr"), IRELAND("ie"), ICELAND("is"),

    ITALY("it"), CROATIA("hr"), NORTH_IRELAND("north_ireland"), AUSTRIA("at"), POLAND("pl"), PORTUGAL("pt"), ROMANIA("ro"),

    RUSSIA("ru"), SWEDEN("se"), SWITZERLAND("ch"), SLOVAKIA("sk"), SPAIN("es"), CZECH_REPUBLIC("cz"), TURKEY("tr"),

    UKRAINE("ua"), HUNGARY("hu"), WALES("wales"), AUSTRALIA("au");

    private static final String ICON_BASE_PATH = "/images/flags/";

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

    public String getIconPath() {
        return ICON_BASE_PATH + isoCode + ".png";
    }
}
