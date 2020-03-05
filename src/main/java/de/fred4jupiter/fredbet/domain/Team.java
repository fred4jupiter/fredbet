package de.fred4jupiter.fredbet.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

@Embeddable
public class Team {

    @Enumerated(EnumType.STRING)
    @Column(name = "COUNTRY")
    private Country country;

    @Column(name = "NAME")
    private String name;

    @Column(name = "GOALS")
    private Integer goals;

    public boolean hasCountrySet() {
        return this.country != null && !Country.NONE.equals(this.country);
    }

    public boolean hasResultSet() {
        return this.goals != null;
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

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGoals() {
        return goals;
    }

    public void setGoals(Integer goals) {
        this.goals = goals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return country == team.country &&
                Objects.equals(name, team.name) &&
                Objects.equals(goals, team.goals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, name, goals);
    }

    @Override
    public String toString() {
        return "Team{" +
                "country=" + country +
                ", name='" + name + '\'' +
                ", goals=" + goals +
                '}';
    }
}
