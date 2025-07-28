package com.EduTech.dto.facility;

import java.time.LocalDate;

import com.EduTech.entity.facility.HolidayReason;

import lombok.Getter;
import lombok.Setter;

//휴무일 등록 및 조회용 DTO
@Getter
@Setter
public class FacilityHolidayDTO {
    private Long holidayId;                // 휴무일 고유 ID
    private LocalDate holidayDate;  // 휴무일 날짜
    private HolidayReason reason;          // 사유 (예: 정기휴무, 공휴일 등)
    private Long facilityNum;       // 대상 시설 번호
}
