package com.EduTech.entity.event;

import java.util.Arrays;

public enum EventState {
	BEFORE("신청전"),
    OPEN("신청중"),
    CLOSED("신청마감"),
    CANCEL("신청취소");

    private final String label;

    EventState(String label) { this.label = label; }

    public String getLabel() { return label; }

    public static EventState fromLabel(String label) {
        return Arrays.stream(values())
                .filter(v -> v.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid label: " + label));
    }
}
