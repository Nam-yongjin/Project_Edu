package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import lombok.Data;

// 클라이언트로 부터 해당 월의 시작날짜, 끝날찌,
// 실증 상품 번호를 받아와 해당 일자가 
// 예약 되있는 상태 리스트를 불러오는 DTO
// (클라이언트 -> 백)
@Data
public class DemonstrationTimeReqDTO { 
	private LocalDate startDate;
	private LocalDate endDate;
	private Long demNum;
}
