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

	private Long demNum; // 물품 번호
	private String demName; // 물품 이름
	private String demInfo; // 물품 설명
	private String demMfr;  // 물품 제조사
	private Long itemNum; // 재고량
	private LocalDate expDate; // 반납 일자
	private String companyName; // 회사명
	private DemonstrationCategory category; // 카테고리
	private List<DemonstrationImageDTO> imageList; // 이미지 리스트
	
	// 실증 장비 신청 상세 페이지용 생성자
	public DemonstrationDetailDTO(Long demNum, String demName, String demInfo,String demMfr, Long itemNum, LocalDate expDate,DemonstrationCategory category,String companyName) {
		this.demNum = demNum;
		this.demName = demName;
		this.demInfo = demInfo;
		this.demMfr=demMfr;
		this.itemNum = itemNum;
		this.expDate = expDate;
		this.category=category;
		this.companyName=companyName;
	}
}
