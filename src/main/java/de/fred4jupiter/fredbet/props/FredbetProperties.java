package de.fred4jupiter.fredbet.props;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fred4jupiter.fredbet.security.UserGroup;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
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

    private Integer diceMinRange = 0;

    private Integer diceMaxRange = 3;

    private Integer thumbnailSize;

    private String adminUsername = "admin";

    private String adminPassword = "admin";

    @NestedConfigurationProperty
    private AuthorizationProperties authorization;

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

    public AuthorizationProperties getAuthorization() {
        return authorization;
    }

    public void setAuthorization(AuthorizationProperties authorization) {
        this.authorization = authorization;
    }
}
