package de.fred4jupiter.fredbet.domain.entity;

import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author michael
 */
@Entity
@Table(name = "APP_USER")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "USER_ID"))
    @Column(name = "ROLE")
    private Set<String> roles;

    @Column(name = "USER_NAME", unique = true)
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    @Column(name = "DELETABLE")
    private boolean deletable = true;

    @Column(name = "IS_CHILD")
    private boolean child;

    @Column(name = "FIRST_LOGIN")
    private boolean firstLogin;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "theme", column = @Column(name = "setting_theme")),
    })
    private AppUserSetting appUserSetting;

    public void addUserGroup(FredBetUserGroup... fredBetUserGroups) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        for (FredBetUserGroup fredBetUserGroup : fredBetUserGroups) {
            this.roles.add(fredBetUserGroup.name());
        }
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }

    public boolean hasPermission(String permission) {
        Collection<? extends GrantedAuthority> authorities = this.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals(permission)) {
                return true;
            }
        }

        return false;
    }

    public boolean isDeletable() {
        return deletable;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isChild() {
        return child;
    }

    public void setChild(boolean child) {
        this.child = child;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("id", id);
        builder.append("username", username);
        builder.append("password", password != null ? "is set" : "is null");
        builder.append("roles", roles);
        builder.append("deletable", deletable);
        builder.append("firstLogin", firstLogin);
        return builder.toString();
    }

    public AppUserSetting getAppUserSetting() {
        return appUserSetting;
    }

    public void setAppUserSetting(AppUserSetting appUserSetting) {
        this.appUserSetting = appUserSetting;
    }
}
