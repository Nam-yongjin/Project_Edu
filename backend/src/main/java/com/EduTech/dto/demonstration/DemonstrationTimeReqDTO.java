package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 클라이언트로 부터 해당 월의 시작날짜, 끝날찌,
// 실증 상품 번호를 받아와 해당 일자가 
// 예약 되있는 상태 리스트를 불러오는 DTO
// (클라이언트 -> 백)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemonstrationTimeReqDTO { 
	private LocalDate startDate;
	private LocalDate endDate;
	private Long demNum;
	
	public DemonstrationTimeReqDTO(LocalDate startDate,LocalDate endDate)
	{
		this.startDate=startDate;
		this.endDate=endDate;
	}
}
