package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.RequestType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 개인 물품 대여 조회 페이지용 DTO (백->클라이언트)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemonstrationRentalListDTO {
	private Long demNum;
	private String demName;
	private Long bItemNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDate applyAt;
	private String companyName;
	private Long itemNum;
	private Long demRevNum;
	// private List<DemonstrationImageDTO> imageList;
	private List<RequestType> requestType;
	private List<DemonstrationState> reqState;// 반납 / 연장 상태
	private DemonstrationState state; // 물품 대여 상태

	public DemonstrationRentalListDTO(Long demNum, String demName, Long bItemNum, LocalDate startDate,
			LocalDate endDate, LocalDate applyAt, String companyName, Long itemNum, Long demRevNum,
			DemonstrationState state) {
		this.demNum = demNum;
		this.demName = demName;
		this.bItemNum = bItemNum;
		this.startDate = startDate;
		this.endDate = endDate;
		this.applyAt = applyAt;
		this.companyName = companyName;
		this.itemNum = itemNum;
		this.demRevNum = demRevNum;
		this.state = state;
	}

}
