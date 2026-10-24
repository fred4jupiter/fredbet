package de.fred4jupiter.fredbet.web.team;

import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntryType;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class TeamCommand {

    private Long teamId;

    private Integer version;

    private boolean useTeamBundleEntry = true;

    // qualified name of the team bundle entry, e.g. COUNTRY:GERMANY
    private String teamBundleEntry;

    @Size(max = 100)
    private String name;

    private boolean favourite;

    private MultipartFile crestFile;

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

    public boolean isUseTeamBundleEntry() {
        return useTeamBundleEntry;
    }

    public void setUseTeamBundleEntry(boolean useTeamBundleEntry) {
        this.useTeamBundleEntry = useTeamBundleEntry;
    }

    public String getTeamBundleEntry() {
        return teamBundleEntry;
    }

    public void setTeamBundleEntry(String teamBundleEntry) {
        this.teamBundleEntry = teamBundleEntry;
    }

    public TeamBundleEntry resolveTeamBundleEntry() {
        return TeamBundleEntryType.fromQualifiedName(teamBundleEntry);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public MultipartFile getCrestFile() {
        return crestFile;
    }

    public void setCrestFile(MultipartFile crestFile) {
        this.crestFile = crestFile;
    }

    public boolean isNew() {
        return teamId == null;
    }
}
