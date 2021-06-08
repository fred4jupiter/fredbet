package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.security.FredBetUserGroup;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Builder pattern for appUsers.
 *
 * @author michael
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
        withUserGroup(FredBetUserGroup.ROLE_USER);
        deletable(true);
        return this;
    }

    public AppUserBuilder withUsernameAndPassword(String username, String password) {
        this.appUser.setUsername(username.trim());
        this.appUser.setPassword(password);
        return this;
    }

    public AppUserBuilder withPassword(String password) {
        this.appUser.setPassword(password);
        return this;
    }

    public AppUserBuilder withIsChild(boolean isChild) {
        this.appUser.setChild(isChild);
        return this;
    }

    public AppUserBuilder withUserGroup(FredBetUserGroup fredBetUserGroup) {
        this.appUser.addUserGroup(fredBetUserGroup);
        return this;
    }

    public AppUserBuilder withRoles(Set<String> roles) {
        this.appUser.setRoles(roles);
        return this;
    }

    public AppUserBuilder withLastLogin(LocalDateTime date) {
        this.appUser.setLastLogin(date);
        return this;
    }

    public AppUserBuilder withUserGroups(Set<String> userGroups) {
        for (String userGroup : userGroups) {
            this.appUser.addUserGroup(FredBetUserGroup.valueOf(userGroup));
        }

        return this;
    }

    public AppUserBuilder withFirstLogin(boolean firstLogin) {
        this.appUser.setFirstLogin(firstLogin);
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
