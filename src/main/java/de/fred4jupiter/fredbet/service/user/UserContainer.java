package de.fred4jupiter.fredbet.service.user;

import java.util.List;

public class UserContainer {

    private List<UserToExport> userList;

    public List<UserToExport> getUserList() {
        return userList;
    }

    public void setUserList(List<UserToExport> userList) {
        this.userList = userList;
    }
}
