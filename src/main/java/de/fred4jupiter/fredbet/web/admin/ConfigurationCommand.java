package de.fred4jupiter.fredbet.web.admin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.context.i18n.LocaleContextHolder;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.util.DateUtils;
import de.fred4jupiter.fredbet.util.LoggingUtil.LogLevel;

public class ConfigurationCommand {

	private List<LogLevel> logLevel = Arrays.asList(LogLevel.values());

	private LogLevel level;

	private String timeZone;

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

	public List<String> getTimeZoneIds() {
		return ZoneId.getAvailableZoneIds().stream().sorted().collect(Collectors.toList());
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getCurrentTime() {
		return DateUtils.formatZonedDateTime(ZonedDateTime.now(), LocaleContextHolder.getLocale());
	}
}
