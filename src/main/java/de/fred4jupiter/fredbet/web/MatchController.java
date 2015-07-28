package de.fred4jupiter.fredbet.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.MatchService;

@Controller
@RequestMapping("/matches")
public class MatchController {

	@Autowired
	private MatchService matchService;

	@RequestMapping
	public ModelAndView list() {
		List<Match> matches = matchService.findAll();
		return new ModelAndView("matches/list", "allMatches", matches);
	}

	@RequestMapping("{id}")
	public ModelAndView edit(@PathVariable("id") String matchId) {
		MatchCommand matchCommand = matchService.findByMatchId(matchId);
		return new ModelAndView("matches/form", "matchCommand", matchCommand);
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createForm(@ModelAttribute MatchCommand matchCommand) {
		return "matches/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid MatchCommand matchCommand, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("matches/form", "formErrors", result.getAllErrors());
		}

		matchService.save(matchCommand);

		String msg = "Spiel " + matchCommand.getTeamNameOne() + " gegen " + matchCommand.getTeamNameTwo() + " angelegt/aktualisiert!";
		redirect.addFlashAttribute("globalMessage", msg);
		return new ModelAndView("redirect:/matches");
	}
}
