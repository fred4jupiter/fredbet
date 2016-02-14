package de.fred4jupiter.fredbet.web;

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
        
        return DEFAULT_MATCHES;
    }

}
