package de.fred4jupiter.fredbet.domain;

public enum NavbarLayout {

    DARK("navbar-dark"),

    LIGHT("navbar-light");

    private final String navbarTag;

    NavbarLayout(String navbarTag) {
        this.navbarTag = navbarTag;
    }

    public String getNavbarTag() {
        return navbarTag;
    }
}
