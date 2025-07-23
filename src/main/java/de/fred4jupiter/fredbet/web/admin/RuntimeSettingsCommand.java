package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.util.DateUtils;
import jakarta.validation.Valid;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class RuntimeSettingsCommand {

    @Valid
    private RuntimeSettings runtimeSettings;

    public List<String> getTimeZoneIds() {
        return ZoneId.getAvailableZoneIds().stream().sorted().toList();
    }

    public String getCurrentTime() {
        return DateUtils.formatZonedDateTime(ZonedDateTime.now(), LocaleContextHolder.getLocale());
    }

    public RuntimeSettings getRuntimeSettings() {
        return runtimeSettings;
    }

    public void setRuntimeSettings(RuntimeSettings runtimeSettings) {
        this.runtimeSettings = runtimeSettings;
    }
}
