package com.EduTech.entity.event;

public enum EventCategory {
	
    USER("일반인"),
    STUDENT("학생"),
    TEACHER("교수");

    private final String label;

    EventCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
}
