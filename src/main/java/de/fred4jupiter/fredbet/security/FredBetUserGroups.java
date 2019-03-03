package de.fred4jupiter.fredbet.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = FredBetUserGroups.PROPS_PREFIX)
public class FredBetUserGroups {

    public static final String PROPS_PREFIX = "fredbet-user-groups";

    private List<UserGroup> groups = new ArrayList<>();

    public List<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getPermissionsForUserGroup(String userGroupName) {
        UserGroup userGroup = getUserGroupByName(userGroupName);
        return userGroup.getPermissions().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private UserGroup getUserGroupByName(String name) {
        for (UserGroup userGroup : groups) {
            if (userGroup.getGroupName().equals(name)) {
                return userGroup;
            }
        }
        throw new IllegalArgumentException("UserGroup with name: " + name + " not found!");
    }
}
