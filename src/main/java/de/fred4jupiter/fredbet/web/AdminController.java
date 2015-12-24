package de.fred4jupiter.fredbet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.fred4jupiter.fredbet.data.DataBasePopulator;

@Controller
@RequestMapping("/administration")
public class AdminController {

	@Autowired
	private DataBasePopulator dataBasePopulator;

	@RequestMapping
	public String list() {
		return "admin/administration";
	}

	@RequestMapping(path = "/createDemoData", method = RequestMethod.GET)
	public String createDemoData() {
		dataBasePopulator.createDemoData();
		return "admin/administration";
	}
}
