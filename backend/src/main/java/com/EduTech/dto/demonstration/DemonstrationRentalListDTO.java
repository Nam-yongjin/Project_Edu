package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import lombok.Data;

//물품 대여 조회 페이지용 DTO (백->클라이언트)
@Data
public class DemonstrationRentalListDTO {
	private Long demNum;
	private String demName;
	private String companyName;
	private Long itemNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDate applyAt;

	// 물품 대여 조회 페이지용 생성자
	public DemonstrationRentalListDTO(Long demNum, String demName, String companyName, Long itemNum,
			LocalDate startDate, LocalDate endDate, LocalDate applyAt) {
		this.demNum = demNum;
		this.demName = demName;
		this.companyName = companyName;
		this.itemNum = itemNum;
		this.startDate = startDate;
		this.endDate = endDate;
		this.applyAt = applyAt;
	}
}
