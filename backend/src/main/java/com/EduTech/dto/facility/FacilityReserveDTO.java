package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.EduTech.entity.facility.FacilityState;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityReserveDTO {
	private Long facilityNum; 		// 시설 고유 번호
    private Long facRevNum;         // 예약 번호
    private String facName;         // 시설명
    private LocalDate facDate;      // 예약일
    private LocalTime startTime;    // 시작 시간
    private LocalTime endTime;      // 종료 시간
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private FacilityState state;	// 예약 상태 (WAIT, ACCEPT, REJECT)
    
    @NotNull	//NotNull 사용이유가 Null값 등록 방지
    private LocalDateTime reserveAt;  // 신청 시각
}