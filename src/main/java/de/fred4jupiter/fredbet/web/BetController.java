package de.fred4jupiter.fredbet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.fred4jupiter.fredbet.service.BettingService;

@Controller
@RequestMapping("/bet")
public class BetController {

	@Autowired
	private BettingService bettingService;
}
