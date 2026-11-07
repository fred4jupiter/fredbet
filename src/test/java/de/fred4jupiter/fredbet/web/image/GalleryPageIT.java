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
    public void showGalleryWithBlueimpGalleryFromWebjar() {
        assertThat(mvc.get().uri("/gallery/show"))
            .hasStatusOk()
            .hasViewName("image/gallery")
            .bodyText().contains(
                "id=\"blueimp-gallery\"",
                "/webjars/blueimp-gallery/3.4.0/css/blueimp-gallery.min",
                "/webjars/blueimp-gallery/3.4.0/js/blueimp-gallery.min");
    }

    @Test
    public void blueimpGalleryWebjarResourcesAreServed() {
        assertThat(mvc.get().uri("/webjars/blueimp-gallery/js/blueimp-gallery.min.js")).hasStatusOk();
        assertThat(mvc.get().uri("/webjars/blueimp-gallery/css/blueimp-gallery.min.css")).hasStatusOk();
        // referenced relatively from the CSS
        assertThat(mvc.get().uri("/webjars/blueimp-gallery/img/close.svg")).hasStatusOk();
        assertThat(mvc.get().uri("/webjars/blueimp-gallery/img/loading.gif")).hasStatusOk();
    }
}
