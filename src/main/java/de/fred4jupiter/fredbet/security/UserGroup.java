package de.fred4jupiter.fredbet.security;

import java.util.HashSet;
import java.util.Set;

public class UserGroup {

    private String groupName;

    private Set<String> permissions = new HashSet<>();

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
