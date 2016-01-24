package de.fred4jupiter.fredbet.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.util.DateUtils;

@Document
public class AppUser implements UserDetails {

    private static final long serialVersionUID = -2973861561213230375L;

    @Id
    private String id;

    private List<String> roles;

    @Indexed(unique = true)
    private String username;

    private String password;

    private Date createdAt;

    private boolean deletable = true;

    @PersistenceConstructor
    protected AppUser() {
        // for mongodb
    }

    public void addRole(FredBetRole... fredBetRoles) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        for (FredBetRole fredBetRole : fredBetRoles) {
            this.roles.add(fredBetRole.name());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        AppUser other = (AppUser) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(id, other.id);
        builder.append(username, other.username);
        builder.append(password, other.password);
        builder.append(roles, other.roles);
        builder.append(deletable, other.deletable);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(id);
        builder.append(username);
        builder.append(password);
        builder.append(roles);
        builder.append(deletable);
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("id", id);
        builder.append("username", username);
        builder.append("password", password != null ? "is set" : "is null");
        builder.append("roles", roles);
        builder.append("deletable", deletable);
        return builder.toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        return roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getRolesAsString() {
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        return roles.stream().map(i -> i.toString()).collect(Collectors.joining(", "));
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public int roleCount() {
        return this.roles.size();
    }

    public LocalDateTime getCreatedAt() {
        return DateUtils.toLocalDateTime(createdAt);
    }
}
