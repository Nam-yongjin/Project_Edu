package com.EduTech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	// 프론트에서 백 폴더에 접근할때 사용
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/view/demImages/**")
	            .addResourceLocations("file:///C:/upload/demImages/");
	    registry.addResourceHandler("/view/event/**")
        		.addResourceLocations("file:///C:/upload/event/");
	    registry.addResourceHandler("/view/Event/mainImage/**")
        		.addResourceLocations("file:///C:/upload/Event/mainImage/");
	    registry.addResourceHandler("/view/facility/**")
        		.addResourceLocations("file:///C:/upload/facility/");
	    registry.addResourceHandler("/view/upload/facility/**")
        		.addResourceLocations("file:///C:/upload/facility/");
	}
	
}
