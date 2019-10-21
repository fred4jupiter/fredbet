package de.fred4jupiter.fredbet.service.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class FontCreator {

    private static final String FONT_NAME = "FreeSans.ttf";

    private final Font font;

    public FontCreator() {
        this.font = createInitialFont();
    }

    Font createFont() {
        return new Font(this.font);
    }

    private Font createInitialFont() {
        try {
            ClassPathResource resource = new ClassPathResource("fonts/" + FONT_NAME);
            byte[] bytes = IOUtils.toByteArray(resource.getInputStream());
            BaseFont baseFont = BaseFont.createFont(FONT_NAME, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
            return new Font(baseFont, 12);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
