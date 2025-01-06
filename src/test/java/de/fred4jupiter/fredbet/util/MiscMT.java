package de.fred4jupiter.fredbet.util;

import de.fred4jupiter.fredbet.domain.Group;
import org.junit.jupiter.api.Test;

public class MiscMT {

    @Test
    public void printAllGroups() {
        for (Group group : Group.values()) {
            System.out.println(group.name());
        }
    }
}
