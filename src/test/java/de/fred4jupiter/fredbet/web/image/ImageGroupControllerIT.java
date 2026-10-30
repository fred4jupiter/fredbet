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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class ImageGroupControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageGroupService imageGroupService;

    @Test
    public void showGroupsWithDeleteButtonOnlyForDeletableGroups() throws Exception {
        when(imageGroupService.findAvailableImageGroups()).thenReturn(List.of(
            imageGroup(1L, "Holiday"), imageGroup(2L, FredbetConstants.DEFAULT_IMAGE_GROUP_NAME)));

        mockMvc.perform(get("/imagegroup/show"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("id=\"imageGroupName-1\""),
                containsString("value=\"Holiday\""),
                containsString("data-id=\"1\""),
                not(containsString("data-id=\"2\"")),
                containsString("fa-lock"),
                containsString("confirm-delete-group"))));
    }

    @Test
    public void saveWithBlankNameShowsErrorAndKeepsGroupList() throws Exception {
        when(imageGroupService.findAvailableImageGroups()).thenReturn(List.of(imageGroup(1L, "Holiday")));
        mockMvc.perform(post("/imagegroup/save").with(csrf())
                .param("id", "1").param("name", " "))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("value=\"Holiday\""),
                containsString("is-invalid"),
                containsString("invalid-feedback"))));

        verify(imageGroupService, never()).updateImageGroup(anyLong(), any());
    }

    @Test
    public void saveWithIdRenamesTheGroup() throws Exception {
        mockMvc.perform(post("/imagegroup/save").with(csrf())
                .param("id", "1").param("name", "Party"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/imagegroup/show"));

        verify(imageGroupService).updateImageGroup(1L, "Party");
    }

    private ImageGroup imageGroup(Long id, String name) {
        ImageGroup imageGroup = new ImageGroup(name);
        ReflectionTestUtils.setField(imageGroup, "id", id);
        return imageGroup;
    }

}
