package de.fred4jupiter.fredbet.imexport;

import java.util.Set;

// TODO: refactor to java record
public class UserToExport {

    private String username;

    private String password;

    private Set<String> roles;

    private boolean child;

    private String userAvatarBase64;

    private String imageGroupName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isChild() {
        return child;
    }

    public void setChild(boolean child) {
        this.child = child;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getUserAvatarBase64() {
        return userAvatarBase64;
    }

    public void setUserAvatarBase64(String userAvatarBase64) {
        this.userAvatarBase64 = userAvatarBase64;
    }

    public String getImageGroupName() {
        return imageGroupName;
    }

    public void setImageGroupName(String imageGroupName) {
        this.imageGroupName = imageGroupName;
    }
}
