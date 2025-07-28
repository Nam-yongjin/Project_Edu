package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.EduTech.entity.facility.FacilityState;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityReserveDTO {
	private Long facilityNum; 		// 시설 고유 번호
    private Long facRevNum;         // 예약 번호
    private String facName;         // 시설명
    private LocalDate facDate;      // 예약일
    private LocalTime startTime;    // 시작 시간
    private LocalTime endTime;      // 종료 시간
    
    @Builder.Default
    private FacilityState state = FacilityState.WAITING;	// 예약 상태 (WAITING, ACCEPT, REJECT) 기본값 : WAITING
    
    @NotNull	//NotNull 사용이유가 Null값 등록 방지
    private LocalDateTime reserveAt;  // 신청 시각
}