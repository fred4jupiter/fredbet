package de.fred4jupiter.fredbet.util;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.props.FredBetProfile;

@Component
public class LoggingUtil {

	private static final String APP_PACKAGE_LOGGER = "de.fred4jupiter.fredbet";

	private static final Logger LOG = LoggerFactory.getLogger(LoggingUtil.class);

	@Autowired
	private Environment environment;

	@PostConstruct
	public void init() {
		if (environment.acceptsProfiles(FredBetProfile.DEV)) {
			setLogLevelTo(LogLevel.DEBUG);
		} else if (environment.acceptsProfiles(FredBetProfile.PROD)) {
			setLogLevelTo(LogLevel.INFO);
		}
	}

	public void setLogLevelTo(LogLevel logLevel) {
		LOG.info("Setting log level to {}", logLevel);
		Configurator.setLevel(APP_PACKAGE_LOGGER, toLevel(logLevel));
	}
	
	public LogLevel getCurrentLogLevel() {
		org.apache.logging.log4j.Logger logger = LogManager.getLogger(APP_PACKAGE_LOGGER);
		return toLogLevel(logger.getLevel());
	}

	private LogLevel toLogLevel(Level level) {
		if (Level.DEBUG.equals(level)) {
			return LogLevel.DEBUG;
		}
		else if (Level.INFO.equals(level)) {
			return LogLevel.INFO;
		}
		else if (Level.WARN.equals(level)) {
			return LogLevel.WARN;
		}
		else if (Level.ERROR.equals(level)) {
			return LogLevel.ERROR;
		}
		return LogLevel.ERROR;
	}

	private Level toLevel(LogLevel logLevel) {
		switch (logLevel) {
		case DEBUG:
			return Level.DEBUG;
		case INFO:
			return Level.INFO;
		case WARN:
			return Level.WARN;
		case ERROR:
			return Level.ERROR;
		default:
			return Level.ERROR;
		}
	}

	public enum LogLevel {
		DEBUG,

		INFO,

		WARN,

		ERROR;
	}
}
