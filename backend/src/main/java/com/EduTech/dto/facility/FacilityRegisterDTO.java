package com.EduTech.dto.facility;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityRegisterDTO {
    private String facName;
    private String facInfo;
    private int capacity;
    private String facItem;
    private String etc;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime reserveStart;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime reserveEnd;
    
}