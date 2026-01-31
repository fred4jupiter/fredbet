package de.fred4jupiter.fredbet.avatar;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.util.TempFileWriterUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class AvatarCreatorServiceMT {

    @InjectMocks
    private AvatarCreatorService avatarCreatorService;

    @Test
    void createAvatar() {
        byte[] avatarImage = avatarCreatorService.createAvatar("Michael");
        assertThat(avatarImage).isNotNull();

        TempFileWriterUtil.writeToTempFolder(avatarImage, "michael.jpg");
    }
}
