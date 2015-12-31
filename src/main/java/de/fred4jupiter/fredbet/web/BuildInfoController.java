package de.fred4jupiter.fredbet.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Properties;

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

	@RequestMapping
	public ModelAndView list() {
		ModelAndView modelAndView = new ModelAndView("buildinfo", "buildInfoMap", buildProperties);
		addDynamicInfoProperties();
		return modelAndView;
	}

	private void addDynamicInfoProperties() {
		addCurrentDateTime();
		addIPAddress();
	}

	private void addIPAddress() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			String hostAddress = localHost.getHostAddress();
			buildProperties.put("hostAddress", hostAddress);
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
