package de.fred4jupiter.fredbet.converter;

import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;

public class LocalDateTimeToStringConverter implements Converter<LocalDateTime, String>{

	@Override
	public String convert(LocalDateTime source) {
		if (source == null) {
			return null;
		}
		return source.toString();
	}

}
