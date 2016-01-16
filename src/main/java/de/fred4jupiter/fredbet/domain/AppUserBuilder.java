package de.fred4jupiter.fredbet.domain;

import java.util.UUID;

import de.fred4jupiter.fredbet.FredBetRole;

/**
 * Builder pattern for appUsers.
 * 
 * @author michael
 *
 */
public class AppUserBuilder {

    private final AppUser appUser;
    
    private AppUserBuilder() {
        this.appUser = new AppUser();
    }
    
    public static AppUserBuilder create() {
        return new AppUserBuilder();
    }
    
    public AppUserBuilder withDemoData() {
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        withUsernameAndPassword(username, password);
        withRoles(FredBetRole.ROLE_USER);
        deletable(true);
        return this;
    }
    
    public AppUserBuilder withUsernameAndPassword(String username, String password) {
        this.appUser.setUsername(username);
        this.appUser.setPassword(password);
        return this;
    }
    
    public AppUserBuilder withPassword(String password) {
        this.appUser.setPassword(password);
        return this;
    }
    
    public AppUserBuilder withRoles(FredBetRole... fredBetRoles) {
        for (FredBetRole fredBetRole : fredBetRoles) {
            this.appUser.addRole(fredBetRole);
        }
        return this;
    }
    
    public AppUserBuilder deletable(boolean deletable) {
        this.appUser.setDeletable(deletable);
        return this;
    }
    
    public AppUser build() {
        return this.appUser;
    }
}
