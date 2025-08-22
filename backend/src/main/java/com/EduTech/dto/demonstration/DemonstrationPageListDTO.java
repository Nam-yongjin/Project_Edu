package com.EduTech.dto.demonstration;

import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.Data;

@Data
public class DemonstrationPageListDTO {
	// 실증 장비 신청 목록 페이지용 dto (백->프론트)

	private Long demNum;
	private String demName;
	private String demMfr;
	private Long itemNum;
	private String companyName;
	private DemonstrationState state;
	private List<DemonstrationImageDTO> imageList;
	public DemonstrationPageListDTO(Long demNum, String demName, String demMfr, Long itemNum,DemonstrationState state,String companyName) {
	    this.demNum = demNum;
	    this.demName = demName;
	    this.demMfr = demMfr;
	    this.itemNum = itemNum;
	    this.state=state;
	    this.companyName=companyName;
}
}
