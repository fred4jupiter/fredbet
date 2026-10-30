package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import de.fred4jupiter.fredbet.image.group.ImageGroupService;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class ImageGroupControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockitoBean
    private ImageGroupService imageGroupService;

    @Test
    public void showGroupsWithDeleteButtonOnlyForDeletableGroups() throws Exception {
        when(imageGroupService.findAvailableImageGroups()).thenReturn(List.of(
            imageGroup(1L, "Holiday"), imageGroup(2L, FredbetConstants.DEFAULT_IMAGE_GROUP_NAME)));

        mockMvc.perform(get("/imagegroup/show").session(englishSession()))
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

        MockHttpSession session = englishSession();
        mockMvc.perform(post("/imagegroup/save").session(session).param("_csrf", csrfToken(session))
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
        MockHttpSession session = englishSession();
        mockMvc.perform(post("/imagegroup/save").session(session).param("_csrf", csrfToken(session))
                .param("id", "1").param("name", "Party"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/imagegroup/show"));

        verify(imageGroupService).updateImageGroup(1L, "Party");
    }

    /**
     * Reads the CSRF token from the rendered page, as a browser would submit it.
     */
    private String csrfToken(MockHttpSession session) throws Exception {
        String html = mockMvc.perform(get("/imagegroup/show").session(session)).andReturn().getResponse().getContentAsString();
        Matcher matcher = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"").matcher(html);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    private ImageGroup imageGroup(Long id, String name) {
        ImageGroup imageGroup = new ImageGroup(name);
        ReflectionTestUtils.setField(imageGroup, "id", id);
        return imageGroup;
    }

    private MockHttpSession englishSession() {
        MockHttpSession session = LoggedInSession.of(userDetailsService, "admin");
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
        return session;
    }
}
