package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class ImageUploadPageIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void showUploadPageWithBootstrapTableFromWebjar() {
        assertThat(mvc.get().uri("/image/show"))
            .hasStatusOk()
            .hasViewName("image/image_upload")
            .bodyText().contains(
                "data-toggle=\"table\"",
                "/webjars/bootstrap-table/1.24.1/dist/bootstrap-table.min");
    }

    @Test
    public void bootstrapTableWebjarResourcesAreServed() {
        assertThat(mvc.get().uri("/webjars/bootstrap-table/dist/bootstrap-table.min.js")).hasStatusOk();
        assertThat(mvc.get().uri("/webjars/bootstrap-table/dist/bootstrap-table.min.css")).hasStatusOk();
    }
}
