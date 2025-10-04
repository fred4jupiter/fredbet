package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.domain.Group;

public enum GroupSelection {

    GROUPS_32,
    GROUPS_16,
    GROUPS_08,
    GROUPS_04,
    GROUPS_02;

    public String getMsgKey() {
        return switch (this) {
            case GROUPS_32 -> Group.ROUND_OF_THIRTY_TWO.getTitleMsgKey();
            case GROUPS_16 -> Group.ROUND_OF_SIXTEEN.getTitleMsgKey();
            case GROUPS_08 -> Group.QUARTER_FINAL.getTitleMsgKey();
            case GROUPS_04 -> Group.SEMI_FINAL.getTitleMsgKey();
            case GROUPS_02 -> Group.FINAL.getTitleMsgKey();
        };
    }

    public Integer getNumberOfGroups() {
        return switch (this) {
            case GROUPS_32 -> 32;
            case GROUPS_16 -> 16;
            case GROUPS_08 -> 8;
            case GROUPS_04 -> 4;
            case GROUPS_02 -> 2;
        };
    }
}
