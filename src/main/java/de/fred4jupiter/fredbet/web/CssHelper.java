package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;

public final class CssHelper {

	private static final String BADGE_PENALTY_WINNER_CSS_CLASS = "badge-penalty-winner";

	private CssHelper() {
		// only static methods
	}

	public static String getCssClassPenaltyWinnerOne(Bet bet) {
		if (bet.isGroupMatch() || !bet.isUndecidedBetting()) {
			return "";
		}
		return bet.isPenaltyWinnerOne() ? BADGE_PENALTY_WINNER_CSS_CLASS : "";
	}

	public static String getCssClassPenaltyWinnerTwo(Bet bet) {
		if (bet.isGroupMatch() || !bet.isUndecidedBetting()) {
			return "";
		}
		return !bet.isPenaltyWinnerOne() ? BADGE_PENALTY_WINNER_CSS_CLASS : "";
	}

	public static String getUserBetGoalsTeamOneCssClasses(MatchCommand matchCommand) {
		if (matchCommand.getUserBetGoalsTeamOne() == null) {
			return "label-default";
		}

		return !matchCommand.isGroupMatch() && matchCommand.isUndecidedBetting() && matchCommand.isPenaltyWinnerOneBet()
				? "label-success badge-penalty-winner" : "label-success";
	}

	public static String getUserBetGoalsTeamTwoCssClasses(MatchCommand matchCommand) {
		if (matchCommand.getUserBetGoalsTeamTwo() == null) {
			return "label-default";
		}

		return !matchCommand.isGroupMatch() && matchCommand.isUndecidedBetting() && !matchCommand.isPenaltyWinnerOneBet()
				? "label-success badge-penalty-winner" : "label-success";
	}

	public static String getTeamResultOneCssClasses(MatchCommand matchCommand) {
		if (matchCommand.getTeamResultOne() == null) {
			return "label-default";
		}

		return !matchCommand.isGroupMatch() && matchCommand.isUndecidedResult() && matchCommand.isPenaltyWinnerOneMatch()
				? "label-info badge-penalty-winner" : "label-info";
	}

	public static String getTeamResultTwoCssClasses(MatchCommand matchCommand) {
		if (matchCommand.getTeamResultTwo() == null) {
			return "label-default";
		}

		return !matchCommand.isGroupMatch() && matchCommand.isUndecidedResult() && !matchCommand.isPenaltyWinnerOneMatch()
				? "label-info badge-penalty-winner" : "label-info";
	}
}
