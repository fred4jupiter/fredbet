package de.fred4jupiter.fredbet.web;

import java.time.LocalDateTime;
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
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM);
		ModelAndView modelAndView = new ModelAndView("buildinfo", "buildInfoMap", buildProperties);
		modelAndView.addObject("currentDateTime", formatter.format(LocalDateTime.now()));
		return modelAndView;
	}
}
