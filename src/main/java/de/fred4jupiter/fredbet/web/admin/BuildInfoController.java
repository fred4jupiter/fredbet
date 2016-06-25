package de.fred4jupiter.fredbet.web.admin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/buildinfo")
public class BuildInfoController {

	private static final Logger LOG = LoggerFactory.getLogger(BuildInfoController.class);

	@Autowired
	private Properties buildProperties;

	@PostConstruct
	private void postProcessBuildProperties() {
		String buildTimestamp = buildProperties.getProperty("build.timestamp");
		buildTimestamp = buildTimestamp + " +0000";

		ZonedDateTime parsed = ZonedDateTime.parse(buildTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm Z"));
		String formattedDateTime = parsed
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm zZ", Locale.getDefault()).withZone(ZoneId.of("Europe/Berlin")));
		buildProperties.put("build.timestamp", formattedDateTime);
	}

	@RequestMapping
	public ModelAndView list() {
		ModelAndView modelAndView = new ModelAndView("buildinfo", "buildInfoMap", buildProperties);
		addDynamicInfoProperties();
		return modelAndView;
	}

	private void addDynamicInfoProperties() {
		addCurrentDateTime();
		addHostName();
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
		final String value = formatter.format(LocalDateTime.now()) + ", " + timeZoneFormatter.format(ZonedDateTime.now());

		buildProperties.put(key, value);
	}
}
