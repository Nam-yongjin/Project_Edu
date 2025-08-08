package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.Data;
import lombok.NoArgsConstructor;

//물품 대여 조회 페이지용 DTO (백->클라이언트)
@Data
@NoArgsConstructor
public class DemonstrationRentalListDTO {
	private Long demNum;
	private String demName;
	private Long bItemNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDate applyAt;
	private String companyName;
	private Long itemNum; 
	private List<DemonstrationImageDTO> imageList; 
	private DemonstrationState state;
	// 물품 대여 조회 페이지용 생성자
	public DemonstrationRentalListDTO(Long demNum, String demName, Long bItemNum,String companyName,
			LocalDate startDate, LocalDate endDate, LocalDate applyAt,DemonstrationState state) {
		this.demNum = demNum;
		this.demName = demName;
		this.bItemNum = bItemNum;
		this.startDate = startDate;
		this.endDate = endDate;
		this.applyAt = applyAt;
		this.state=state;
	}
}
