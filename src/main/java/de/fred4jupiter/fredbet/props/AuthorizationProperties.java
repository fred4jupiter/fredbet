package de.fred4jupiter.fredbet.props;

import de.fred4jupiter.fredbet.security.UserGroup;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record AuthorizationProperties(Set<UserGroup> userGroups) {

    public Collection<? extends GrantedAuthority> getPermissionsForUserGroup(String userGroupName) {
        UserGroup userGroup = getUserGroupByName(userGroupName);
        return userGroup.permissions().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    private UserGroup getUserGroupByName(String name) {
        for (UserGroup userGroup : this.userGroups) {
            if (userGroup.groupName().equals(name)) {
                return userGroup;
            }
        }
        throw new IllegalArgumentException("UserGroup with name: " + name + " not found!");
    }
}
