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
    private Long reserveId;        // 예약 번호
    private Long facRevNum;        // 시설 번호
    private String facName;
    private String memId;          // 신청자 ID (관리자 화면은 보여줘도 OK)
    private LocalDate facDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private FacilityState state;
    private LocalDateTime reserveAt;
}

