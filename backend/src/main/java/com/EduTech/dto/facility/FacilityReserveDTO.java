package com.EduTech.dto.facility;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityReserveDTO {

    private Long facRevNum;         	// 예약 정보 번호
    
    @NotNull	//NotNull 사용이유가 Null값 등록 방지
    private LocalDateTime reserveAt; 	// 신청일
    private LocalDateTime startDate; 	// 시작 일시
    private LocalDateTime endDate;   	// 종료 일시
    private String state;            	// 상태 (WAIT, ACCEPT, REJECT)
    private Long facilityNum;        	// 시설 번호
    private String memId;            	// 회원 아이디
    
}