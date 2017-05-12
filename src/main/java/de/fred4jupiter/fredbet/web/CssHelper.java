package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.web.matches.MatchCommand;

public final class CssHelper {

	private static final String LABEL_INFO = "label-info";

	private static final String LABEL_SUCCESS = "label-success";

	private static final String LABEL_DEFAULT = "label-default";

	private static final String LABEL_SUCCESS_PENALTY = LABEL_SUCCESS + " " + FredbetConstants.BADGE_PENALTY_WINNER_CSS_CLASS;

	private static final String LABEL_INFO_PENALTY = LABEL_INFO + " " + FredbetConstants.BADGE_PENALTY_WINNER_CSS_CLASS;

	private CssHelper() {
		// only static methods
	}

	public static String getUserBetGoalsTeamOneCssClasses(MatchCommand matchCommand) {
		if (matchCommand.getUserBetGoalsTeamOne() == null) {
			return LABEL_DEFAULT;
		}

		return !matchCommand.isGroupMatch() && matchCommand.isUndecidedBetting() && matchCommand.isPenaltyWinnerOneBet()
				? LABEL_SUCCESS_PENALTY : LABEL_SUCCESS;
	}

	public static String getUserBetGoalsTeamTwoCssClasses(MatchCommand matchCommand) {
		if (matchCommand.getUserBetGoalsTeamTwo() == null) {
			return LABEL_DEFAULT;
		}

		return !matchCommand.isGroupMatch() && matchCommand.isUndecidedBetting() && !matchCommand.isPenaltyWinnerOneBet()
				? LABEL_SUCCESS_PENALTY : LABEL_SUCCESS;
	}

	public static String getTeamResultOneCssClasses(MatchCommand matchCommand) {
		if (matchCommand.getTeamResultOne() == null) {
			return LABEL_DEFAULT;
		}

		return !matchCommand.isGroupMatch() && matchCommand.isUndecidedResult() && matchCommand.isPenaltyWinnerOneMatch()
				? LABEL_INFO_PENALTY : LABEL_INFO;
	}

	public static String getTeamResultTwoCssClasses(MatchCommand matchCommand) {
		if (matchCommand.getTeamResultTwo() == null) {
			return LABEL_DEFAULT;
		}

		return !matchCommand.isGroupMatch() && matchCommand.isUndecidedResult() && !matchCommand.isPenaltyWinnerOneMatch()
				? LABEL_INFO_PENALTY : LABEL_INFO;
	}
}
