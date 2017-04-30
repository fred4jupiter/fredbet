package de.fred4jupiter.fredbet.web.admin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/buildinfo")
public class BuildInfoController {

	private static final String BUILDINFO_VIEW = "admin/buildinfo";

	private static final Logger LOG = LoggerFactory.getLogger(BuildInfoController.class);

	@Autowired
	private Properties buildProperties;

	@Autowired
	private MetricsEndpoint metricsEndpoint;

	@Autowired
	private Environment environment;

	@PostConstruct
	private void postProcessBuildProperties() {
		addBuildTimestamp();
		addSpringProfiles();
	}

	@RequestMapping
	public ModelAndView list() {
		ModelAndView modelAndView = new ModelAndView(BUILDINFO_VIEW, "buildInfoMap", buildProperties);
		addDynamicInfoProperties();
		return modelAndView;
	}

	private void addBuildTimestamp() {
		String buildTimestamp = buildProperties.getProperty("build.timestamp");
		buildTimestamp = buildTimestamp + " +0000";
		ZonedDateTime parsed = ZonedDateTime.parse(buildTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm Z"));
		String formattedDateTime = parsed.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm zZ", Locale.getDefault())
				.withZone(ZoneId.of("Europe/Berlin")));
		buildProperties.put("build.timestamp", formattedDateTime);
	}

	private void addDynamicInfoProperties() {
		addCurrentDateTime();
		addHostName();
		addMetrics();
	}

	private void addSpringProfiles() {
		String[] activeProfiles = environment.getActiveProfiles();
		buildProperties.put("Active Profiles", activeProfiles != null ? Arrays.asList(activeProfiles) : "");
	}

	private void addMetrics() {
		Map<String, Object> metricsMap = metricsEndpoint.invoke();

		buildProperties.put("system.uptime", formatUptime((Long) metricsMap.get("uptime")));
		buildProperties.put("application.context.uptime", formatUptime((Long) metricsMap.get("instance.uptime")));
	}

	private String formatUptime(Long millis) {
		return String.format("%02d:%02d [HH:mm]", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis));
	}

	private void addHostName() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			String hostName = localHost.getHostName();
			buildProperties.put("hostName", hostName);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void addCurrentDateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);
		DateTimeFormatter timeZoneFormatter = DateTimeFormatter.ofPattern("VV x");

		final String key = "currentDateTime";
		final String value = formatter.format(LocalDateTime.now()) + ", "
				+ timeZoneFormatter.format(ZonedDateTime.now());

		buildProperties.put(key, value);
	}
}
