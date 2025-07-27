package com.EduTech.entity.facility;

public enum FacilityState {
	WAITING("대기"),
    APPROVED("수락"),
    REJECTED("거절"),
	CANCELLED("취소");

    private final String label;

    FacilityState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
