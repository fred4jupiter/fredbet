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
import de.fred4jupiter.fredbet.repository.MatchRepository;

@Controller
@RequestMapping("/matches")
public class MatchController {

	@Autowired
	private MatchRepository matchRepository;

	@RequestMapping
	public ModelAndView list() {
		List<Match> matches = matchRepository.findAll();
		return new ModelAndView("matches/list", "allMatches", matches);
	}

	@RequestMapping("{id}")
	public ModelAndView edit(@PathVariable("id") String matchId) {
		Match match = matchRepository.findOne(matchId);
		MatchCommand matchCommand = toMatchCommand(match);
		
		return new ModelAndView("matches/form", "matchCommand", matchCommand);
	}

	private MatchCommand toMatchCommand(Match match) {
		MatchCommand matchCommand = new MatchCommand();
		matchCommand.setMatchId(match.getId());
		matchCommand.setTeamNameOne(match.getTeamOne().getName());
		matchCommand.setTeamNameTwo(match.getTeamTwo().getName());
		matchCommand.setTeamResultOne(match.getResult().getGoalsTeamOne());
		matchCommand.setTeamResultTwo(match.getResult().getGoalsTeamTwo());
		return matchCommand;
	}

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(@ModelAttribute MatchCommand matchCommand) {
		return "matches/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid MatchCommand matchCommand, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("matches/form", "formErrors", result.getAllErrors());
		}

		Match match = matchRepository.findOne(matchCommand.getMatchId());
		if (match == null) {
			match = new Match();
		}

		match.getTeamOne().setName(matchCommand.getTeamNameOne());
		match.getTeamTwo().setName(matchCommand.getTeamNameTwo());
		match.getResult().setGoalsTeamOne(matchCommand.getTeamResultOne());
		match.getResult().setGoalsTeamTwo(matchCommand.getTeamResultTwo());

		match = this.matchRepository.save(match);
		matchCommand.setMatchId(match.getId());
		redirect.addFlashAttribute("globalMessage", "Successfully created a new message");
		return new ModelAndView("redirect:/matches/{matchCommand.id}", "matchCommand.id", matchCommand.getMatchId());
	}
}
