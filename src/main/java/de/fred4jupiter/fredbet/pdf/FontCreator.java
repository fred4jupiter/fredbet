package de.fred4jupiter.fredbet.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.FontProvider;
import com.lowagie.text.pdf.BaseFont;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.IOException;

@Component
class FontCreator {

    public static final String FONT_NAME = "FreeSans.ttf";

    private final Font font;

    public FontCreator(@Value("classpath:/fonts/" + FONT_NAME) Resource fontResource) {
        this.font = createInitialFont(fontResource);
    }

    Font createFont() {
        return new Font(this.font);
    }

    /**
     * Provides the embedded font for every font face, so that the HTML parser does not fall back to Helvetica, which
     * can not display all characters of the supported languages.
     */
    FontProvider createFontProvider() {
        final BaseFont baseFont = this.font.getBaseFont();
        return new FontProvider() {
            @Override
            public Font getFont(String fontName, String encoding, boolean embedded, float size, int style, Color color) {
                return new Font(baseFont, size, style, color);
            }

            @Override
            public boolean isRegistered(String fontName) {
                return true;
            }
        };
    }

    private Font createInitialFont(Resource fontResource) {
        try {
            byte[] bytes = IOUtils.toByteArray(fontResource.getInputStream());
            BaseFont baseFont = BaseFont.createFont(FONT_NAME, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
            return new Font(baseFont, 12);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
