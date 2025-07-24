package com.EduTech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.EduTech.controller.formatter.LocalDateFormatter;
import com.EduTech.controller.formatter.LocalDateTimeFormatter;
import com.EduTech.controller.formatter.LocalTimeFormatter;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	// 날짜 포맷 지정
	@Override
	public void addFormatters(FormatterRegistry registry) {

		registry.addFormatter(new LocalDateFormatter());
		registry.addFormatter(new LocalDateTimeFormatter());
		registry.addFormatter(new LocalTimeFormatter());
	}
	
}
