package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.util.LogLevel;

import java.util.Arrays;
import java.util.List;

public class ConfigurationCommand {

    private List<LogLevel> logLevel = Arrays.asList(LogLevel.values());

    private LogLevel level;

    public List<LogLevel> getLogLevel() {
        return logLevel;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }
}
