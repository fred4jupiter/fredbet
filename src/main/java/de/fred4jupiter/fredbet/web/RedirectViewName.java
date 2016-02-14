package de.fred4jupiter.fredbet.web;

import org.apache.commons.lang3.StringUtils;

public enum RedirectViewName {

    DEFAULT_MATCHES("redirect:/matches"),

    OPEN_BETS("redirect:/bet/open");

    private String redirectViewName;

    private RedirectViewName(String redirectViewName) {
        this.redirectViewName = redirectViewName;
    }

    public String getRedirectViewName() {
        return redirectViewName;
    }

    public static RedirectViewName fromName(String name) {
        for (RedirectViewName redirectViewNames : values()) {
            if (redirectViewNames.name().equalsIgnoreCase(name)) {
                return redirectViewNames;
            }
        }

        throw new IllegalArgumentException("Could not resolve to RedirectViewName. Name " + name + " is unsupported!");
    }

    public static String resolveRedirect(String redirectViewNameString) {
        if (StringUtils.isBlank(redirectViewNameString)) {
            return RedirectViewName.DEFAULT_MATCHES.getRedirectViewName();
        }

        try {
            return RedirectViewName.fromName(redirectViewNameString).getRedirectViewName();
        } catch (IllegalArgumentException e) {
            return RedirectViewName.DEFAULT_MATCHES.getRedirectViewName();
        }
    }

}
