package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import de.fred4jupiter.fredbet.image.group.ImageGroupService;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class ImageGroupControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ImageGroupService imageGroupService;

    @Test
    public void showGroupsWithDeleteButtonOnlyForDeletableGroups() {
        when(imageGroupService.findAvailableImageGroups()).thenReturn(List.of(
            imageGroup(1L, "Holiday"), imageGroup(2L, FredbetConstants.DEFAULT_IMAGE_GROUP_NAME)));

        assertThat(mvc.get().uri("/imagegroup/show"))
            .hasStatusOk()
            .bodyText()
            .contains(
                "id=\"imageGroupName-1\"",
                "value=\"Holiday\"",
                "data-id=\"1\"",
                "fa-lock",
                "confirm-delete-group")
            .doesNotContain("data-id=\"2\"");
    }

    @Test
    public void saveWithBlankNameShowsErrorAndKeepsGroupList() {
        when(imageGroupService.findAvailableImageGroups()).thenReturn(List.of(imageGroup(1L, "Holiday")));

        assertThat(mvc.post().uri("/imagegroup/save").with(csrf())
                .param("id", "1").param("name", " "))
            .hasStatusOk()
            .bodyText().contains(
                "value=\"Holiday\"",
                "is-invalid",
                "invalid-feedback");

        verify(imageGroupService, never()).updateImageGroup(anyLong(), any());
    }

    @Test
    public void saveWithIdRenamesTheGroup() {
        assertThat(mvc.post().uri("/imagegroup/save").with(csrf())
                .param("id", "1").param("name", "Party"))
            .hasStatus3xxRedirection()
            .hasRedirectedUrl("/imagegroup/show");

        verify(imageGroupService).updateImageGroup(1L, "Party");
    }

    private ImageGroup imageGroup(Long id, String name) {
        ImageGroup imageGroup = new ImageGroup(name);
        ReflectionTestUtils.setField(imageGroup, "id", id);
        return imageGroup;
    }

}
