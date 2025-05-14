package de.fred4jupiter.fredbet.web.info;

public enum InfoType {

    RULES("info/rules", "rules.title"),

    PRICES("info/prices", "prices.title"),

    MISC("info/misc", "misc.title");

    private final String pageName;

    private final String msgKey;

    InfoType(String pageName, String msgKey) {
        this.pageName = pageName;
        this.msgKey = msgKey;
    }

    public String getPageName() {
        return pageName;
    }

    public String getMsgKey() {
        return msgKey;
    }
}
