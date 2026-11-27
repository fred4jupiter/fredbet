package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
public class KickOffDateParserUT {

    private static final LocalDateTime EXPECTED = LocalDateTime.of(2026, 6, 11, 21, 0);

    @Mock
    private RuntimeSettingsService runtimeSettingsService;

    @InjectMocks
    private KickOffDateParser kickOffDateParser;

    @Test
    public void detectCommonFormats() {
        assertThat(kickOffDateParser.parse("2026-06-11T21:00", null)).isEqualTo(EXPECTED);
        assertThat(kickOffDateParser.parse("2026-06-11 21:00:00", null)).isEqualTo(EXPECTED);
        assertThat(kickOffDateParser.parse("11.06.2026 21:00", null)).isEqualTo(EXPECTED);
        assertThat(kickOffDateParser.parse("11/06/2026 21:00", null)).isEqualTo(EXPECTED);
    }

    @Test
    public void zonedDateIsConvertedToConfiguredTimeZone() {
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        runtimeSettings.setTimeZone("Europe/Berlin");
        when(runtimeSettingsService.loadRuntimeSettings()).thenReturn(runtimeSettings);

        assertThat(kickOffDateParser.parse("2026-06-11T19:00:00Z", null)).isEqualTo(EXPECTED);
    }

    @Test
    public void customDateFormat() {
        assertThat(kickOffDateParser.parse("06-11-2026 9:00 PM", "MM-dd-yyyy h:mm a")).isEqualTo(EXPECTED);
        assertThat(kickOffDateParser.parse("2026-06-11T21:00", "dd.MM.yyyy HH:mm")).isNull();
    }

    @Test
    public void invalidValues() {
        assertThat(kickOffDateParser.parse("tomorrow", null)).isNull();
        assertThat(kickOffDateParser.isValidDateFormat("dd.MM.yyyy HH:mm")).isTrue();
        assertThat(kickOffDateParser.isValidDateFormat("")).isTrue();
        assertThat(kickOffDateParser.isValidDateFormat("{invalid")).isFalse();
    }
}
