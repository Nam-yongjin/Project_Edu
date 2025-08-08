package com.EduTech.dto.facility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityRegisterDTO {
    private String facName;
    private int capacity;
    private String facItem;
    private String etc;
    private String facInfo;
}