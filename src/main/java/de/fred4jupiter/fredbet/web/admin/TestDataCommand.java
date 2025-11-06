package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.data.GroupSelection;

import java.util.List;

public class TestDataCommand {

    private Boolean includeBets;

    private Boolean includeResults;

    private final List<GroupSelection> groupSelections = List.of(GroupSelection.values());

    private GroupSelection groupSelection;

    private Boolean createGameOfThird;

    public Boolean getIncludeBets() {
        return includeBets;
    }

    public void setIncludeBets(Boolean includeBets) {
        this.includeBets = includeBets;
    }

    public Boolean getIncludeResults() {
        return includeResults;
    }

    public void setIncludeResults(Boolean includeResults) {
        this.includeResults = includeResults;
    }

    public List<GroupSelection> getGroupSelections() {
        return groupSelections;
    }

    public GroupSelection getGroupSelection() {
        return groupSelection;
    }

    public void setGroupSelection(GroupSelection groupSelection) {
        this.groupSelection = groupSelection;
    }

    public Boolean getCreateGameOfThird() {
        return createGameOfThird;
    }

    public void setCreateGameOfThird(Boolean createGameOfThird) {
        this.createGameOfThird = createGameOfThird;
    }
}
