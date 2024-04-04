package de.fred4jupiter.fredbet.security;

import java.util.Set;

public record UserGroup(String groupName, Set<String> permissions) {

}
