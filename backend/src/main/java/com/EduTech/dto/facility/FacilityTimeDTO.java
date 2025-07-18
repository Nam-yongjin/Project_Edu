package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityTimeDTO {

    private Long facTimeNum;    	// 예약 시간 번호
    private LocalDate facDate;  	// 날짜
    private LocalTime startTime; 	// 시작 시간
    private LocalTime endTime;   	// 종료 시간
    private Boolean state;       	// 예약 가능 여부
    private Long facilityNum;    	// 시설 번호
    
}