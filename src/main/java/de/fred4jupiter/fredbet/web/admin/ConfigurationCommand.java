package de.fred4jupiter.fredbet.web.admin;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.util.LoggingUtil.LogLevel;

public class ConfigurationCommand {

	private List<LogLevel> logLevel = Arrays.asList(LogLevel.values());

	private LogLevel level;
	
	@Valid
	private RuntimeConfig runtimeConfig;

	public List<LogLevel> getLogLevel() {
		return logLevel;
	}

	public LogLevel getLevel() {
		return level;
	}

	public void setLevel(LogLevel level) {
		this.level = level;
	}

	public RuntimeConfig getRuntimeConfig() {
		return runtimeConfig;
	}

	public void setRuntimeConfig(RuntimeConfig runtimeConfig) {
		this.runtimeConfig = runtimeConfig;
	}

}
