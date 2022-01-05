package de.fred4jupiter.fredbet.util;

import ch.qos.logback.classic.Level;
import de.fred4jupiter.fredbet.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingUtil implements LogLevelChangable {

    private static final String APP_PACKAGE_LOGGER = Application.class.getPackageName();

    private static final Logger LOG = LoggerFactory.getLogger(LoggingUtil.class);

    @Override
    public void setLogLevelTo(LogLevel logLevel) {
        LOG.info("Setting log level to {}", logLevel);
        getAppPackageLogger().setLevel(toLevel(logLevel));
    }

    @Override
    public LogLevel getCurrentLogLevel() {
        return toLogLevel(getAppPackageLogger().getLevel());
    }

    private ch.qos.logback.classic.Logger getAppPackageLogger() {
        return (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(APP_PACKAGE_LOGGER);
    }

    private LogLevel toLogLevel(Level level) {
        if (Level.DEBUG.equals(level)) {
            return LogLevel.DEBUG;
        } else if (Level.INFO.equals(level)) {
            return LogLevel.INFO;
        } else if (Level.WARN.equals(level)) {
            return LogLevel.WARN;
        } else if (Level.ERROR.equals(level)) {
            return LogLevel.ERROR;
        }
        return LogLevel.ERROR;
    }

    private Level toLevel(LogLevel logLevel) {
        return switch (logLevel) {
            case DEBUG -> Level.DEBUG;
            case INFO -> Level.INFO;
            case WARN -> Level.WARN;
            default -> Level.ERROR;
        };
    }
}
