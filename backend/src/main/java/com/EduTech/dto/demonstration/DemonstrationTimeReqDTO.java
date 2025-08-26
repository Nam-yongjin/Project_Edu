package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemonstrationTimeReqDTO { 
	private LocalDate startDate; // 해당 월의 시작날짜
	private LocalDate endDate; // 해당 월의 끝 날짜
	private Long demNum; // 상품 번호
	
	public DemonstrationTimeReqDTO(LocalDate startDate,LocalDate endDate)
	{
		this.startDate=startDate;
		this.endDate=endDate;
	}
}
