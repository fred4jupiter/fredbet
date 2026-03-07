package de.fred4jupiter.fredbet.integration;

public record Competition(Integer id, String name, String code, int seasonYear) {

    public String getKey() {
        return code + "_" + seasonYear;
    }
}
