package de.fred4jupiter.fredbet.domain.entity;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Locale;

@Entity
@Table(name = "TEAM")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "COUNTRY", unique = true)
    private Country country;

    @Column(name = "NAME", unique = true)
    private String name;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "CRESTS_BINARY")
    @Lob
    private byte[] crestsBinary;

    public Long getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getNameTranslated(MessageSourceUtil messageSourceUtil, Locale locale) {
        if (this.country == null) {
            return name;
        }

        return messageSourceUtil.getCountryName(this.country, locale);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        return new EqualsBuilder().append(id, team.id).append(country, team.country).append(name, team.name).append(crestsBinary, team.crestsBinary).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(country).append(name).append(crestsBinary).toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("country", country);
        builder.append("name", name);
        return builder.build();
    }

    public String getBusinessKey() {
        return "team_%s_%s".formatted(this.country, this.name);
    }

    public byte[] getCrestsBinary() {
        return crestsBinary;
    }

    public void setCrestsBinary(byte[] crestsBinary) {
        this.crestsBinary = crestsBinary;
    }
}
