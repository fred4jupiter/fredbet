package de.fred4jupiter.fredbet.web.admin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.context.i18n.LocaleContextHolder;

import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.util.DateUtils;

public class RuntimeConfigCommand {

	@Valid
	private RuntimeConfig runtimeConfig;

	private String timeZone;

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

	public RuntimeConfig getRuntimeConfig() {
		return runtimeConfig;
	}

	public void setRuntimeConfig(RuntimeConfig runtimeConfig) {
		this.runtimeConfig = runtimeConfig;
	}
}
