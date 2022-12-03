package de.fred4jupiter.fredbet.web.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
