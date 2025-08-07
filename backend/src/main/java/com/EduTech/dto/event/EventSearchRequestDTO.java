package com.EduTech.dto.event;

import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventSearchRequestDTO {
	
    private String searchType;       // "eventName", "eventInfo", "all"
    
    private String keyword;          // 검색어
    
    private EventState state;        // "OPEN", "BEFORE", "CLOSED", "CANCEL"
    
    private EventCategory category;  // "TEACHER", "STUDENT", "USER"
    
    private String sortOrder = "DESC"; // 기본값

}