package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class EditInfoIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void showEditorWithSummernoteFromWebjar() {
        assertThat(mvc.get().uri("/info/editinfo/rules"))
            .hasStatusOk()
            .hasViewName("info/edit_info")
            .bodyText().contains(
                "id=\"summernote\"",
                "/webjars/summernote/0.9.1/dist/summernote-lite.min",
                "js/pages/edit_info");
    }

    @Test
    public void summernoteWebjarResourcesAreServed() {
        assertThat(mvc.get().uri("/webjars/summernote/dist/summernote-lite.min.js")).hasStatusOk();
        assertThat(mvc.get().uri("/webjars/summernote/dist/summernote-lite.min.css")).hasStatusOk();
        assertThat(mvc.get().uri("/webjars/summernote/dist/font/summernote.woff2")).hasStatusOk();
    }
}
