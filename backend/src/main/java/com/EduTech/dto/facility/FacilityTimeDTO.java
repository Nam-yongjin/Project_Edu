package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityTimeDTO {
    private LocalDate facDate;      // 날짜
    private LocalTime startTime;    // 시작 시간
    private LocalTime endTime;      // 종료 시간
    private boolean available;      // 예약 가능 여부
}