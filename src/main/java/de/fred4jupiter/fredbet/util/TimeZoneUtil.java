package de.fred4jupiter.fredbet.util;

import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class TimeZoneUtil {

    private static final Logger LOG = LoggerFactory.getLogger(TimeZoneUtil.class);

    public void checkIfTimezoneIsCorrect(String preferedTimeZoneId) {
        final String defaultTimeZone = getTimeZoneId();

        if (StringUtils.isNotBlank(preferedTimeZoneId) && preferedTimeZoneId.equals(defaultTimeZone)) {
            return;
        }

        LOG.debug("setting timezone to: {}", preferedTimeZoneId);
        setDefaultTimeZone(preferedTimeZoneId);
    }

    public String getTimeZoneId() {
        String timeZoneId = TimeZone.getDefault().getID();
        return StringUtils.isNotBlank(timeZoneId) ? timeZoneId : FredbetConstants.DEFAULT_TIMEZONE;
    }

    public void setDefaultTimeZone(String timeZoneId) {
        if (StringUtils.isBlank(timeZoneId)) {
            LOG.warn("timeZoneId is null");
            return;
        }
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        LOG.info("Setting timeZone to: {}", timeZone.getID());
        TimeZone.setDefault(timeZone);
    }
}
