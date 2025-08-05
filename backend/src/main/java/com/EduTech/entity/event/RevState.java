package com.EduTech.entity.event;

public enum RevState {
    APPROVED("수락"),
    WAITTING("대기"),
    CANCEL("취소");

    private final String label;

    RevState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
