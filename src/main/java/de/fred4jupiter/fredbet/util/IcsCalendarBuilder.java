package de.fred4jupiter.fredbet.util;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class IcsCalendarBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(IcsCalendarBuilder.class);

    private static final String DEFAULT_TIME_ZONE = ZoneId.systemDefault().getId();

    private String timeZone = DEFAULT_TIME_ZONE;

    private LocalDateTime start;

    private LocalDateTime end;

    private String title;

    private String content;

    private String location;

    private final UidGenerator uidGenerator = new RandomUidGenerator();

    private IcsCalendarBuilder() {

    }

    public static IcsCalendarBuilder create() {
        return new IcsCalendarBuilder();
    }

    public IcsCalendarBuilder withTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public IcsCalendarBuilder withStartEnd(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
        return this;
    }

    public IcsCalendarBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public IcsCalendarBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public IcsCalendarBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    public byte[] build() {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone(timeZone);
        VTimeZone tz = timezone.getVTimeZone();
        VEvent vEvent = new VEvent(toDate(this.start), toDate(this.end), this.title);
        vEvent.getProperties().add(tz.getTimeZoneId());
        vEvent.getProperties().add(uidGenerator.generateUid());
        vEvent.getProperties().add(new Description(this.content));
        vEvent.getProperties().add(new Location(this.location));

        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        icsCalendar.getProperties().add(new ProdId("-//FredBet//iCal4j 1.0//EN"));
        icsCalendar.getProperties().add(CalScale.GREGORIAN);
        icsCalendar.getProperties().add(Version.VERSION_2_0);

        icsCalendar.getComponents().add(vEvent);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(icsCalendar, out);
            return out.toByteArray();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private net.fortuna.ical4j.model.DateTime toDate(LocalDateTime localDateTime) {
        Date utilDate = Date.from(localDateTime.atZone(ZoneId.of(this.timeZone)).toInstant());
        return new net.fortuna.ical4j.model.DateTime(utilDate);
    }
}
