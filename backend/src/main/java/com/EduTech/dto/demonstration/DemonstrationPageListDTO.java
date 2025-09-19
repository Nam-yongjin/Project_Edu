package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.Data;

@Data
public class DemonstrationPageListDTO {
	// 실증 장비 신청 목록 페이지용 dto (백->프론트)

	private Long demNum; // 상품 번호
	private String demName; // 상품 이름
	private String demMfr; // 상품 제조사
	private Long itemNum; // 재고량
	private String companyName; // 회사명
	private LocalDate expDate; // 반납 날짜
	private DemonstrationState state; // 상태값
	private List<DemonstrationImageDTO> imageList; // 이미지 리스트
	public DemonstrationPageListDTO(Long demNum, String demName, String demMfr, Long itemNum,DemonstrationState state,String companyName,LocalDate expDate) {
	    this.demNum = demNum;
	    this.demName = demName;
	    this.demMfr = demMfr;
	    this.itemNum = itemNum;
	    this.state=state;
	    this.companyName=companyName;
	    this.expDate=expDate;
}
}
