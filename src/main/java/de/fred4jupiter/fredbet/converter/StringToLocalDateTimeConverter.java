package de.fred4jupiter.fredbet.converter;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

public class StringToLocalDateTimeConverter implements Converter<String,LocalDateTime>{

	@Override
	public LocalDateTime convert(String source) {
		if (StringUtils.isBlank(source)) {
			return null;
		}
		
		return LocalDateTime.parse(source);
	}

}
