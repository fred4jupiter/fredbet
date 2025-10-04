package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.domain.Group;

public enum GroupSelection {

    ROUND_OF_THIRTY_TWO,
    ROUND_OF_SIXTEEN;

    public String getMsgKey() {
        return switch (this) {
            case ROUND_OF_THIRTY_TWO -> Group.ROUND_OF_THIRTY_TWO.getTitleMsgKey();
            case ROUND_OF_SIXTEEN -> Group.ROUND_OF_SIXTEEN.getTitleMsgKey();
        };
    }

    public Integer getNumberOfGroups() {
        return switch (this) {
            case ROUND_OF_THIRTY_TWO -> 32;
            case ROUND_OF_SIXTEEN -> 16;
        };
    }
}
