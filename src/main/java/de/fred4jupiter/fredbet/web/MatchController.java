package de.fred4jupiter.fredbet.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.MatchRepository;

@Controller
public class MatchController {

	@Autowired
	private MatchRepository matchRepository;
	
	@ModelAttribute("allMatches")
	public List<Match> populateMatches() {
		return matchRepository.findAll();
	}

	@RequestMapping(value = "/matches", method = RequestMethod.GET)
	public String showMatches() {
		return "match";
	}
	
	@RequestMapping(value = "/matches", method = RequestMethod.POST)
	public String updateMatches(final BindingResult bindingResult) {
		return "match";
	}
}
