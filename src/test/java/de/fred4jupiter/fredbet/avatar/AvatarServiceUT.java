package de.fred4jupiter.fredbet.avatar;

import de.fred4jupiter.fredbet.common.UnitTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class AvatarServiceUT {

    private final AvatarService avatarService = new AvatarService();

    @Test
    public void selectUserAvatarForNewUserLoadsAvatarBytes() {
        byte[] avatar = avatarService.selectUserAvatarForNewUser();

        assertThat(avatar).isNotNull();
        assertThat(avatar.length).isGreaterThan(0);
    }
}

