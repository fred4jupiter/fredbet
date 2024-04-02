package de.fred4jupiter.fredbet.props;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fred4jupiter.fredbet.security.UserGroup;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains all configuration properties for FredBet application.
 *
 * @author michael
 */
@ConfigurationProperties(prefix = "fredbet")
public class FredbetProperties {

    /**
     * Selection of possible image storage locations.
     */
    private ImageLocation imageLocation = ImageLocation.DATABASE;

    /**
     * Path in file system to store images in case of image location is set to
     * 'file-system'
     */
    private String imageFileSystemBaseFolder;

    private String defaultLanguage;

    private boolean createDemoData;

    private Integer diceMinRange = 0;

    private Integer diceMaxRange = 3;

    private Integer thumbnailSize;

    private String adminUsername = "admin";

    private String adminPassword = "admin";

    private Set<UserGroup> userGroups = new HashSet<>();

    public ImageLocation getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(ImageLocation imageLocation) {
        this.imageLocation = imageLocation;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("imageLocation", imageLocation)
                .append("imageFileSystemBaseFolder", imageFileSystemBaseFolder)
                .append("defaultLanguage", defaultLanguage)
                .append("createDemoData", createDemoData)
                .append("diceMinRange", diceMinRange)
                .append("diceMaxRange", diceMaxRange)
                .toString();
    }

    public String getImageFileSystemBaseFolder() {
        return imageFileSystemBaseFolder;
    }

    public void setImageFileSystemBaseFolder(String imageFileSystemBaseFolder) {
        this.imageFileSystemBaseFolder = imageFileSystemBaseFolder;
    }

    public Locale getDefaultLocale() {
        if (StringUtils.isBlank(defaultLanguage)) {
            return Locale.getDefault();
        }

        return Locale.of(defaultLanguage);
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public boolean isCreateDemoData() {
        return createDemoData;
    }

    public void setCreateDemoData(boolean createDemoData) {
        this.createDemoData = createDemoData;
    }

    public Integer getDiceMinRange() {
        return diceMinRange;
    }

    public void setDiceMinRange(Integer diceMinRange) {
        this.diceMinRange = diceMinRange;
    }

    public Integer getDiceMaxRange() {
        return diceMaxRange;
    }

    public void setDiceMaxRange(Integer diceMaxRange) {
        this.diceMaxRange = diceMaxRange;
    }

    public Integer getThumbnailSize() {
        return thumbnailSize;
    }

    public void setThumbnailSize(Integer thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }

    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getPermissionsForUserGroup(String userGroupName) {
        UserGroup userGroup = getUserGroupByName(userGroupName);
        return userGroup.getPermissions().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    private UserGroup getUserGroupByName(String name) {
        for (UserGroup userGroup : this.userGroups) {
            if (userGroup.getGroupName().equals(name)) {
                return userGroup;
            }
        }
        throw new IllegalArgumentException("UserGroup with name: " + name + " not found!");
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
