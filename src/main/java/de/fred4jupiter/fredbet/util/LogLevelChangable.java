package de.fred4jupiter.fredbet.util;

public interface LogLevelChangable {

    void setLogLevelTo(LogLevel logLevel);

    LogLevel getCurrentLogLevel();
}
