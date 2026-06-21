package de.fred4jupiter.fredbet.image.group;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class ImageGroupServiceUT {

    @InjectMocks
    private ImageGroupService imageGroupService;

    @Mock
    private ImageGroupRepository imageGroupRepository;

    @Test
    public void deleteImageGroupRejectsProtectedGroups() {
        ImageGroup imageGroup = new ImageGroup("gallery", true);
        when(imageGroupRepository.findById(1L)).thenReturn(Optional.of(imageGroup));

        assertThatThrownBy(() -> imageGroupService.deleteImageGroup(1L))
            .isInstanceOf(ImageGroupNotDeletableException.class);

        verify(imageGroupRepository, never()).deleteById(1L);
    }

    @Test
    public void addImageGroupRejectsExistingName() {
        when(imageGroupRepository.findByName("summer")).thenReturn(new ImageGroup("summer"));

        assertThatThrownBy(() -> imageGroupService.addImageGroup("summer"))
            .isInstanceOf(ImageGroupExistsException.class);

        verify(imageGroupRepository, never()).save(any(ImageGroup.class));
    }

    @Test
    public void updateImageGroupRejectsMissingGroup() {
        when(imageGroupRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageGroupService.updateImageGroup(5L, "new-name"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("id=5");
    }

    @Test
    public void updateImageGroupSavesRenamedGroup() {
        ImageGroup imageGroup = new ImageGroup("old-name");
        when(imageGroupRepository.findById(6L)).thenReturn(Optional.of(imageGroup));
        when(imageGroupRepository.findByName("new-name")).thenReturn(null);

        imageGroupService.updateImageGroup(6L, "new-name");

        verify(imageGroupRepository).save(imageGroup);
    }
}

