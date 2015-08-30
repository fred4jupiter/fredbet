package de.fred4jupiter.fredbet.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller("/buildinfo")
public class BuildInfoController {

	@Autowired
	private Environment environment;

	@RequestMapping
	public ModelAndView list() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("version", environment.getProperty("version"));
		map.put("build_date", environment.getProperty("build.date"));
		ModelAndView modelAndView = new ModelAndView("buildinfo", "buildInfoMap", map);
		return modelAndView;
	}
}
