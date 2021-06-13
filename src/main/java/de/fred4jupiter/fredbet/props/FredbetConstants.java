package de.fred4jupiter.fredbet.props;

import de.fred4jupiter.fredbet.domain.Country;

/**
 * Misc constants used in FredBet.
 *
 * @author michael
 */
public final class FredbetConstants {

    private FredbetConstants() {
        // only constant definitions here
    }

    public static final String BASE_PACKAGE = "de.fred4jupiter.fredbet";

    public static final String TECHNICAL_USERNAME = "admin";

    public static final String BADGE_PENALTY_WINNER_BET_CSS_CLASS = "badge-penalty-winner-bet";

    public static final String BADGE_PENALTY_WINNER_MATCH_CSS_CLASS = "badge-penalty-winner-match";

    public static final String JOKER_CSS_CLASS = "joker-betting";

    public static final Country DEFAULT_FAVOURITE_COUNTRY = Country.GERMANY;

    public static final String DEFAULT_REST_PASSWORT = "fredbet";

    public static final String IMAGE_JPG_EXTENSION = "jpg";

    public static final String IMAGE_JPG_EXTENSION_WITH_DOT = "." + IMAGE_JPG_EXTENSION;

    public static final String DEFAULT_IMAGE_GROUP_NAME = "Misc";

    public static final String GALLERY_NAME = "Users";

    public static final int USERNAME_MAX_LENGTH = 12;

    public static final int PASSWORD_MAX_LENGTH = 100;

}
