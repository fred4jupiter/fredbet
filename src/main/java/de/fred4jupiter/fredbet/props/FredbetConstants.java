package de.fred4jupiter.fredbet.props;

import de.fred4jupiter.fredbet.domain.Country;

/**
 * Misc constants used in FredBet.
 * 
 * @author michael
 *
 */
public interface FredbetConstants {

	String TECHNICAL_USERNAME = "admin";

	String INFO_CONTEXT_RULES = "rules";

	String INFO_CONTEXT_PRICES = "prices";

	String INFO_CONTEXT_MISC = "misc";

	String BADGE_PENALTY_WINNER_BET_CSS_CLASS = "badge-penalty-winner-bet";

	String BADGE_PENALTY_WINNER_MATCH_CSS_CLASS = "badge-penalty-winner-match";
	
	String JOKER_CSS_CLASS = "joker-betting";

	Country DEFAULT_FAVOURITE_COUNTRY = Country.GERMANY;

	String DEFAULT_REST_PASSWORT = "fredbet";

}
