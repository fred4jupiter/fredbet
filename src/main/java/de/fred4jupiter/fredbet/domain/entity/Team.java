package de.fred4jupiter.fredbet.domain.entity;

import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.domain.TranslationAware;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntryType;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.context.i18n.LocaleContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

@Entity
@Table(name = "TEAM")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TEAM_TYPE")
    private TeamBundleEntryType teamType;

    // name of the team bundle entry, e.g. the country enum name
    @Column(name = "TEAM_KEY")
    private String teamKey;

    @Column(name = "NAME", unique = true)
    private String name;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "SVG_CONTENT", columnDefinition = "TEXT")
    @Lob
    private String svgContent;

    @Column(name = "FAVOURITE", nullable = false)
    private boolean favourite;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    protected Team() {
        // for hibernate
    }

    public Team(TeamBundleEntry teamBundleEntry) {
        setTeamBundleEntry(teamBundleEntry);
    }

    public Team(String name) {
        this.name = name;
    }

    public String getCrestsAsBase64() {
        if (this.svgContent == null) {
            return null;
        }
        String base64EncodedImage = Base64.getEncoder().encodeToString(this.svgContent.getBytes(StandardCharsets.UTF_8));
        return "data:image/svg+xml;base64," + base64EncodedImage;
    }

    public Long getId() {
        return id;
    }

    public TeamBundleEntry getTeamBundleEntry() {
        if (teamType == null || teamKey == null) {
            return null;
        }
        return teamType.resolve(teamKey);
    }

    public void setTeamBundleEntry(TeamBundleEntry teamBundleEntry) {
        this.teamType = teamBundleEntry != null ? teamBundleEntry.getType() : null;
        this.teamKey = teamBundleEntry != null ? teamBundleEntry.name() : null;
    }

    public String getName() {
        return name;
    }

    public String getNameTranslated(TranslationAware translationAware) {
        return getNameTranslated(translationAware, LocaleContextHolder.getLocale());
    }

    public String getNameTranslated(TranslationAware translationAware, Locale locale) {
        TeamBundleEntry teamBundleEntry = getTeamBundleEntry();
        if (teamBundleEntry == null) {
            return name;
        }

        return translationAware.getTeamBundleEntryName(teamBundleEntry, locale);
    }

    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            this.name = name;
        } else {
            this.name = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        return new EqualsBuilder().append(id, team.id).append(teamType, team.teamType).append(teamKey, team.teamKey).append(name, team.name).append(svgContent, team.svgContent).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(teamType).append(teamKey).append(name).append(svgContent).toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("teamType", teamType);
        builder.append("teamKey", teamKey);
        builder.append("name", name);
        builder.append("favourite", favourite);
        return builder.build();
    }

    public String getBusinessKey() {
        return "team_%s_%s_%s".formatted(this.teamType, this.teamKey, this.name);
    }

    public String getSvgContent() {
        return svgContent;
    }

    public void setSvgContent(SvgImage svgImage) {
        this.svgContent = svgImage.svgContent();
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Integer getVersion() {
        return version;
    }
}
