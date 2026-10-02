package de.fred4jupiter.fredbet.integration;

import java.io.Serializable;

public record Competition(Integer id, String name, String code, int seasonYear, String type) implements Serializable {

}
