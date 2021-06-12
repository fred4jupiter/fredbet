package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.util.DateUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.Valid;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RuntimeSettingsCommand {

    @Valid
    private RuntimeSettings runtimeSettings;

    public List<String> getTimeZoneIds() {
        return ZoneId.getAvailableZoneIds().stream().sorted().collect(Collectors.toList());
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
