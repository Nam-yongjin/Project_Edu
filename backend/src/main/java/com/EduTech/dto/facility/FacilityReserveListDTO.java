package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalTime;

import com.EduTech.entity.facility.FacilityState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityReserveListDTO {
    private Long facRevNum;         // 예약 번호
    private String facName;         // 시설명
    private LocalDate facDate;      // 예약일
    private LocalTime startTime;
    private LocalTime endTime;
    private FacilityState state; // WAIT / ACCEPT / REJECT
}
