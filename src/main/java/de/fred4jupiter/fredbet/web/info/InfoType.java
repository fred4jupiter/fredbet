package de.fred4jupiter.fredbet.web.info;

public enum InfoType {

    RULES("info/rules"),

    PRICES("info/prices"),

    MISC("info/misc");

    private final String pageName;

    InfoType(String pageName) {
        this.pageName = pageName;
    }

    public String getPageName() {
        return pageName;
    }
}
