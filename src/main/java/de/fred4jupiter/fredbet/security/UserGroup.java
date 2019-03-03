package de.fred4jupiter.fredbet.security;

import java.util.ArrayList;
import java.util.List;

public class UserGroup {

    private String groupName;

    private List<String> permissions = new ArrayList<>();

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
