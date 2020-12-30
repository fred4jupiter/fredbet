package de.fred4jupiter.fredbet.web.admin;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class AdminFormCommand {

    @NotNull
    @Max(100)
    @Positive
    private Integer numberOfTestUsers;

    public Integer getNumberOfTestUsers() {
        return numberOfTestUsers;
    }

    public void setNumberOfTestUsers(Integer numberOfTestUsers) {
        this.numberOfTestUsers = numberOfTestUsers;
    }
}
