package de.fred4jupiter.fredbet.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {

    @Override
    public LocalDateTime convert(Date source) {
        return source == null ? null : LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
    }
}
