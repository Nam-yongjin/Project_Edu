package com.EduTech.controller.formatter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;

public class LocalTimeFormatter implements Formatter<LocalTime> {
	@Override
	public LocalTime parse(String text, Locale locale) {
		return LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	@Override
	public String print(LocalTime object, Locale locale) {
		return DateTimeFormatter.ofPattern("HH:mm:ss").format(object);
	}
}
