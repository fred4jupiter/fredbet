package de.fred4jupiter.fredbet.web.calendar;

import de.fred4jupiter.fredbet.service.calendar.IcsCalendarService;
import de.fred4jupiter.fredbet.service.calendar.IcsFile;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ics")
public class IcsCalendarController {

    private static final String CONTENT_TYPE = "text/calendar";

    private final IcsCalendarService icsCalendarService;

    public IcsCalendarController(IcsCalendarService icsCalendarService) {
        this.icsCalendarService = icsCalendarService;
    }

    @GetMapping(value = "/{matchId}", produces = CONTENT_TYPE)
    public ResponseEntity<byte[]> downloadIcsCalendarFile(@PathVariable Long matchId) {
        final IcsFile icsFile = icsCalendarService.createCalendarEventFromMatch(matchId, LocaleContextHolder.getLocale());

        if (icsFile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(icsFile.fileName(), icsFile.binary(), CONTENT_TYPE);
    }

}
