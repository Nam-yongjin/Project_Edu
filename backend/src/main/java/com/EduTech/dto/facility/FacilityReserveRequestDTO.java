package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/* 시설 예약 요청 DTO (프론트에서 예약 요청 시 사용) */
@Getter
@Setter
@Builder
public class FacilityReserveRequestDTO {
    private String facName;         // 시설명.
    private LocalDate facDate;      // 예약 날짜
    private LocalTime startTime;    // 시작 시간
    private LocalTime endTime;      // 종료 시간
    private String memId;           // 신청자 ID
}
