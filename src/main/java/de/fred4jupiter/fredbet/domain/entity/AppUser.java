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
import java.util.stream.Collectors;

/**
 * @author michael
 */
@Entity
@Table(name = "app_user")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    /**
     * The permissions resolved from the roles at login. They are never persisted, so the roles stay user groups.
     */
    @Transient
    private Set<String> permissions;

    @Column(name = "user_name", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "deletable", nullable = false)
    private boolean deletable = true;

    @Column(name = "is_child", nullable = false)
    private boolean child;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "theme", column = @Column(name = "setting_theme", nullable = false)),
        @AttributeOverride(name = "bootswatchTheme", column = @Column(name = "setting_bootswatch_theme", nullable = false)),
        @AttributeOverride(name = "navbarLayout", column = @Column(name = "setting_navbar_layout", nullable = false)),
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

    /**
     * @return the permissions of the logged-in user, see {@link #setPermissions(Collection)}. Use {@link #getRoles()} for the user groups.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(permissions)) {
            return Collections.emptyList();
        }
        return permissions.stream().map(SimpleGrantedAuthority::new).toList();
    }

    public void setPermissions(Collection<? extends GrantedAuthority> permissions) {
        this.permissions = permissions.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
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
