package de.fred4jupiter.fredbet.web;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/buildinfo")
public class BuildInfoController {

	@Autowired
	private Properties buildProperties;

	@RequestMapping
	public ModelAndView list() {
		ModelAndView modelAndView = new ModelAndView("buildinfo", "buildInfoMap", buildProperties);

		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM);
		modelAndView.addObject("currentDateTime", formatter.format(LocalDateTime.now()));
		
		DateTimeFormatter timeZoneFormatter = DateTimeFormatter.ofPattern("VV x");
		modelAndView.addObject("timeZone", timeZoneFormatter.format(ZonedDateTime.now()));
		return modelAndView;
	}
}
