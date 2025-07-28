package com.EduTech.entity.facility;

public enum HolidayReason {
	HOLIDAY("공휴일"),
    MAINTENANCE("정기점검"),
    INTERNAL("기관사정"),
    OTHER("기타");

    private final String label;

    HolidayReason(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
        
    }
}