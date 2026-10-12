package de.fred4jupiter.fredbet.team;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class SvgCrestValidatorUT {

    @Test
    public void validSvg() {
        assertThat(isValid("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 10 10\"><circle r=\"5\" fill=\"red\"/></svg>")).isTrue();
    }

    @Test
    public void validSvgWithDoctype() {
        assertThat(isValid("""
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
            <svg xmlns="http://www.w3.org/2000/svg"><rect width="1" height="1"/></svg>
            """)).isTrue();
    }

    @Test
    public void noSvgRootElement() {
        assertThat(isValid("<html><body/></html>")).isFalse();
    }

    @Test
    public void noXml() {
        assertThat(isValid("this is no svg")).isFalse();
        assertThat(SvgCrestValidator.isValidSvg(new byte[0])).isFalse();
        assertThat(SvgCrestValidator.isValidSvg(null)).isFalse();
    }

    @Test
    public void scriptIsRejected() {
        assertThat(isValid("<svg xmlns=\"http://www.w3.org/2000/svg\"><script>alert(1)</script></svg>")).isFalse();
    }

    @Test
    public void eventHandlerIsRejected() {
        assertThat(isValid("<svg xmlns=\"http://www.w3.org/2000/svg\" onload=\"alert(1)\"/>")).isFalse();
    }

    @Test
    public void javascriptLinkIsRejected() {
        assertThat(isValid("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><a xlink:href=\"java script:alert(1)\"/></svg>")).isFalse();
    }

    @Test
    public void foreignObjectIsRejected() {
        assertThat(isValid("<svg xmlns=\"http://www.w3.org/2000/svg\"><foreignObject><div/></foreignObject></svg>")).isFalse();
    }

    @Test
    public void externalEntityIsRejected() {
        assertThat(isValid("""
            <?xml version="1.0"?>
            <!DOCTYPE svg [<!ENTITY xxe SYSTEM "file:///etc/passwd">]>
            <svg xmlns="http://www.w3.org/2000/svg"><text>&xxe;</text></svg>
            """)).isFalse();
    }

    private boolean isValid(String svg) {
        return SvgCrestValidator.isValidSvg(svg.getBytes(StandardCharsets.UTF_8));
    }
}
