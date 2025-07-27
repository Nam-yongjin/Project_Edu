package com.EduTech.entity.facility;

public enum FacilityCategory {
	CONFERENCE("회의실"),
	SEMINAR("강의실");
	
    private final String label;

    FacilityCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
