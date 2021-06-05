package de.fred4jupiter.fredbet.props;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

/**
 * Contains all configuration properties for FredBet application.
 *
 * @author michael
 */
@ConfigurationProperties(prefix = FredbetProperties.PROPS_PREFIX)
public class FredbetProperties {

    public static final String PROPS_PREFIX = "fredbet";

    /**
     * Selection of possible image storage locations.
     */
    private ImageLocation imageLocation;

    /**
     * Path in file system to store images in case of image location is set to
     * 'file-system'
     */
    private String imageFileSystemBaseFolder;

    private String defaultLanguage;

    private boolean createDemoData;

    private Integer diceMinRange = 0;

    private Integer diceMaxRange = 3;

    public ImageLocation getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(ImageLocation imageLocation) {
        this.imageLocation = imageLocation;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("imageLocation", imageLocation);
        builder.append("imageFileSystemBaseFolder", imageFileSystemBaseFolder);
        return builder.toString();
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

        return new Locale(defaultLanguage);
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
}
