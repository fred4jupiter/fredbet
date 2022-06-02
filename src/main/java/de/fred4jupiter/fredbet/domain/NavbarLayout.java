package de.fred4jupiter.fredbet.domain;

public enum NavbarLayout {

    INVERSE("navbar-inverse"),

    DEFAULT("navbar-default");

    private final String navbarTag;

    NavbarLayout(String navbarTag) {
        this.navbarTag = navbarTag;
    }

    public String getNavbarTag() {
        return navbarTag;
    }
}
