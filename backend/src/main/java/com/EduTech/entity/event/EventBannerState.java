package com.EduTech.entity.event;

public enum EventBannerState {
	YES("배너 등록"),
    NO("배너 등록");

    private final String label;

    EventBannerState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
