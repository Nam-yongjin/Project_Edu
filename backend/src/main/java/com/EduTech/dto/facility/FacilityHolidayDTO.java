package com.EduTech.dto.facility;

import java.time.LocalDate;

import com.EduTech.entity.facility.HolidayReason;

import lombok.Getter;
import lombok.Setter;

// 휴무일 등록 및 조회용 DTO
@Getter
@Setter
public class FacilityHolidayDTO {
	
    private Long holidayId;
    
    private LocalDate holidayDate;
    
    private HolidayReason reason;

    private Long facRevNum;
    
}
