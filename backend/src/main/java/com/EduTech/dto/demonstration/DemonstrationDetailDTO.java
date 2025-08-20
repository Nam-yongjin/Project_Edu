package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationCategory;

import lombok.Data;
import lombok.NoArgsConstructor;

// 실증 장비 신청 상세 페이지용 dto (백->클라이언트)
@Data
@NoArgsConstructor
public class DemonstrationDetailDTO {

	private Long demNum;
	private String demName;
	private String demInfo;
	private String demMfr; 
	private Long itemNum;
	private LocalDate expDate;
	private DemonstrationCategory category;
	private List<DemonstrationImageDTO> imageList;
	
	// 실증 장비 신청 상세 페이지용 생성자
	public DemonstrationDetailDTO(Long demNum, String demName, String demInfo,String demMfr, Long itemNum, LocalDate expDate,DemonstrationCategory category) {
		this.demNum = demNum;
		this.demName = demName;
		this.demInfo = demInfo;
		this.demMfr=demMfr;
		this.itemNum = itemNum;
		this.expDate = expDate;
		this.category=category;
	}
}
