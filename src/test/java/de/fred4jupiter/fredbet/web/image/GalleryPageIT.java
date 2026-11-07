package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class GalleryPageIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void showGalleryWithPhotoSwipeFromWebjar() {
        assertThat(mvc.get().uri("/gallery/show"))
            .hasStatusOk()
            .hasViewName("image/gallery")
            .bodyText().contains(
                "id=\"links\"",
                "/webjars/photoswipe/5.4.3/dist/photoswipe",
                "/webjars/photoswipe/5.4.3/dist/umd/photoswipe.umd.min",
                "js/pages/gallery")
            .doesNotContain("blueimp");
    }

    @Test
    public void photoSwipeWebjarResourcesAreServed() {
        assertThat(mvc.get().uri("/webjars/photoswipe/dist/umd/photoswipe.umd.min.js")).hasStatusOk();
        assertThat(mvc.get().uri("/webjars/photoswipe/dist/photoswipe.css")).hasStatusOk();
    }
}
