package de.fred4jupiter.fredbet.web;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/buildinfo")
public class BuildInfoController {

	@Autowired
	private Properties gitProperties;

	@RequestMapping
	public ModelAndView list() {
		ModelAndView modelAndView = new ModelAndView("buildinfo", "buildInfoMap", gitProperties);
		return modelAndView;
	}
}
