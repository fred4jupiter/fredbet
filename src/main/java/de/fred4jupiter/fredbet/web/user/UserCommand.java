package de.fred4jupiter.fredbet.web.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.ui.ModelMap;

import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

public class UserCommand {

    private Long userId;

    private String username;

    private String password;

    private boolean deletable;
    
    private boolean resetPassword;

    private List<String> roles = new ArrayList<>();

    private final List<String> availableRoles = new ArrayList<>();

    public UserCommand() {
        List<FredBetRole> fredBetRoles = Arrays.asList(FredBetRole.values());
        this.availableRoles.addAll(fredBetRoles.stream().map(role -> role.name()).collect(Collectors.toList()));
        this.roles.add(FredBetRole.ROLE_USER.name());
    }

    public List<String> getAvailableRoles() {
        return availableRoles;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean validate(WebMessageUtil messageUtil, ModelMap modelMap) {
        if (StringUtils.isEmpty(this.username)) {
            messageUtil.addErrorMsg(modelMap, "user.validation.emptyUsername");
            return true;
        }
        if (this.username.contains(" ")) {
            messageUtil.addErrorMsg(modelMap, "user.validation.username.containsBlanks");
            return true;
        }
        final int fieldLength = 12;
		if (this.username.length() > fieldLength) {
            messageUtil.addErrorMsg(modelMap, "user.validation.username.tooLong", fieldLength);
            return true;
        }
        if (StringUtils.isEmpty(this.password)) {
            messageUtil.addErrorMsg(modelMap, "user.validation.password");
            return true;
        }
        if (Validator.isEmpty(this.roles)) {
            messageUtil.addErrorMsg(modelMap, "user.validation.roles");
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("userId", userId);
        builder.append("password", password != null ? "is set" : "is null");
        builder.append("roles", roles);
        return builder.toString();
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(boolean resetPassword) {
        this.resetPassword = resetPassword;
    }

}
