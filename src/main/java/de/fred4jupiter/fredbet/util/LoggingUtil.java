package de.fred4jupiter.fredbet.util;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.props.FredBetProfile;

@Component
public class LoggingUtil {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingUtil.class);

	@Autowired
	private Environment environment;

	@PostConstruct
	public void init() {
		if (environment.acceptsProfiles(FredBetProfile.DEV)) {
			setLogLevelTo(LogLevel.DEBUG);
		} else if (environment.acceptsProfiles(FredBetProfile.PROD)) {
			setLogLevelTo(LogLevel.ERROR);
		}
	}

	public void setLogLevelTo(LogLevel logLevel) {
		LOG.info("Setting log level to {}", logLevel);
		Configurator.setLevel("de.fred4jupiter.fredbet", toLevel(logLevel));
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
