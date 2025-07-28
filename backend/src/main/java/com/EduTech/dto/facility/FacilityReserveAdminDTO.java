package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.EduTech.entity.facility.FacilityState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityReserveAdminDTO {
    private Long facRevNum;         // 예약 번호.
    private String facName;         // 시설명
    private String memId;           // 신청자 ID
    private LocalDate facDate;      // 예약일
    private LocalTime startTime;	// 시작시간
    private LocalTime endTime;		// 종료시간
    private FacilityState state; 	// WAIT / ACCEPT / REJECT
    private LocalDateTime reserveAt; // 신청 시간
}