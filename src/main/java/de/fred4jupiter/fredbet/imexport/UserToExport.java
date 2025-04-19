package de.fred4jupiter.fredbet.imexport;

import java.util.Set;

public record UserToExport(String username, String password, Set<String> roles, boolean child,
                           String userAvatarBase64) {

}
