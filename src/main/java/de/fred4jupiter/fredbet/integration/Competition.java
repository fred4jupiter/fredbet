package de.fred4jupiter.fredbet.integration;

import java.io.Serializable;

public record Competition(Integer id, String name, String code, int seasonYear) implements Serializable {

    public String getKey() {
        return code + "_" + seasonYear;
    }
}
