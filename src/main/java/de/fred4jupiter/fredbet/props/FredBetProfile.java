package de.fred4jupiter.fredbet.props;

/**
 * All available spring profiles within this application.
 *
 * @author michael
 */
public final class FredBetProfile {

    private FredBetProfile() {
    }

    public static final String DEV = "dev";

    public static final String INTEGRATION_TEST = "integration_test";

    public static final String H2 = "h2";
}
