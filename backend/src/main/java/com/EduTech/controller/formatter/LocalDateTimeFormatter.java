package com.EduTech.controller.formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

	@Override
	public LocalDateTime parse(String text, Locale locale) {
		return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	@Override
	public String print(LocalDateTime object, Locale locale) {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(object);
	}
}
