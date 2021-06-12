package de.fred4jupiter.fredbet.service.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.TimeZone;

@Service
public class TimeZoneService {

    private static final Logger LOG = LoggerFactory.getLogger(TimeZoneService.class);

    public void setTimeZone(String timeZoneId) {
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        LOG.info("Setting timeZone to: {}", timeZone.getID());
        TimeZone.setDefault(timeZone);
    }

    public String getTimeZone() {
        return TimeZone.getDefault().getID();
    }
}
