package de.fred4jupiter.fredbet.domain;

public enum Theme {

    DEFAULT("navbar-inverse"),
    CERULEAN("navbar-inverse"),
    COSMO("navbar-inverse"),
    CYBORG("navbar-inverse"),
    DARKLY("navbar-inverse"),
    FLATLY("navbar-inverse"),
    JOURNAL("navbar-inverse"),
    LUMEN("navbar-inverse"),
    PAPER("navbar-inverse"),
    READABLE("navbar-default"),
    SANDSTONE("navbar-inverse"),
    SIMPLEX("navbar-inverse"),
    SLATE("navbar-inverse"),
    SPACELAB("navbar-inverse"),
    SUPERHERO("navbar-inverse"),
    UNITED("navbar-inverse"),
    YETI("navbar-inverse");

    private final String navbarTag;

    Theme(String navbarTag) {
        this.navbarTag = navbarTag;
    }

    public String getNavbarTag() {
        return navbarTag;
    }
}
