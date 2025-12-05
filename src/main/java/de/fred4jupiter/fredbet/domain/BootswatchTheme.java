package de.fred4jupiter.fredbet.domain;

public enum BootswatchTheme {

    DEFAULT,
    CERULEAN,
    COSMO,
    CYBORG,
    DARKLY,
    FLATLY,
    JOURNAL,
    LUMEN,
    MATERIA,
    MINTY,
    PULSE,
    SANDSTONE,
    SIMPLEX,
    SLATE,
    SOLAR,
    SPACELAB,
    SUPERHERO,
    UNITED,
    VAPOR,
    ZEPHYR;

    public String getBootswatchThemeUrl() {
        final String selectedTheme = this.name().toLowerCase();
        if (BootswatchTheme.DEFAULT.name().equalsIgnoreCase(selectedTheme)) {
            return "/webjars/bootstrap/css/bootstrap.min.css";
        }
        return "/webjars/bootswatch/" + selectedTheme + "/bootstrap.min.css";
    }
}
