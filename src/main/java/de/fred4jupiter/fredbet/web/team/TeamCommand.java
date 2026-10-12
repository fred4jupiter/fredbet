package de.fred4jupiter.fredbet.web.team;

import de.fred4jupiter.fredbet.domain.Country;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class TeamCommand {

    private Long teamId;

    private Integer version;

    private boolean useCountry = true;

    private Country country;

    @Size(max = 100)
    private String name;

    private MultipartFile crestFile;

    private boolean deletable;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isUseCountry() {
        return useCountry;
    }

    public void setUseCountry(boolean useCountry) {
        this.useCountry = useCountry;
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

    public MultipartFile getCrestFile() {
        return crestFile;
    }

    public void setCrestFile(MultipartFile crestFile) {
        this.crestFile = crestFile;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isNew() {
        return teamId == null;
    }
}
