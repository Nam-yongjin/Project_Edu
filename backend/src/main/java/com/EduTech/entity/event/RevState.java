package com.EduTech.entity.event;

public enum RevState {
	WAITING("대기"),
    APPROVED("수락"),
    REJECTED("거절");

    private final String label;

    RevState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
