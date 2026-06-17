package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class ImageCommandMapperUT {

    private final ImageCommandMapper imageCommandMapper = new ImageCommandMapper();

    @Test
    public void toListOfImageCommandReturnsEmptyListForNoImages() {
        assertThat(imageCommandMapper.toListOfImageCommand(List.of())).isEmpty();
    }

    @Test
    public void toListOfImageCommandMapsAndSortsByGalleryGroup() {
        ImageMetaData beta = createImageMetaData(2L, "img-2", "desc-2", "beta");
        ImageMetaData alpha = createImageMetaData(1L, "img-1", "desc-1", "alpha");

        List<ImageCommand> commands = imageCommandMapper.toListOfImageCommand(List.of(beta, alpha));

        assertThat(commands).extracting(ImageCommand::getGalleryGroup).containsExactly("alpha", "beta");
        assertThat(commands.getFirst().getImageKey()).isEqualTo("img-1");
    }

    private ImageMetaData createImageMetaData(Long id, String key, String description, String groupName) {
        ImageMetaData imageMetaData = new ImageMetaData(key, new ImageGroup(groupName),
            AppUserBuilder.create().withUsernameAndPassword("alfredo", "pw").build());
        imageMetaData.setDescription(description);
        try {
            java.lang.reflect.Field idField = ImageMetaData.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(imageMetaData, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return imageMetaData;
    }
}

