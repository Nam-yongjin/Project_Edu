package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DemonstrationListDTO { // 실증 장비 목록 페이지 데이터 받는 dto (상세아님)

	private Long demNum; // 실증번호 (demonstration의 기본키)
	private String demName; // 물품명
	private String demInfo; // 믈품소개
	private String demMfr; // 제조사
	private Long itemNum; // 개수
	private LocalDate expDate; // 반납예정일
	private String comPanyName; // 기업명
	private LocalDate startDate; // 사용시작일
	private LocalDate endDate; // 사용종료일
	private LocalDate applyAt; // 실증 신청일
	private String imageName; // 이미지 이름
	private String imageUrl; // 파일 경로
	// 이미지 이름 + 파일 경로가 전체 경로
	
	// 실증 장비 신청 목록 페이지용 생성자
	public DemonstrationListDTO(Long demNum, String demName, String demMfr, Long itemNum,String imageName,String imageUrl) {
		this.demNum = demNum;
		this.demName = demName;
		this.demMfr = demMfr;
		this.itemNum = itemNum;
		this.imageName=imageName;
		this.imageUrl=imageUrl;
	}

	// 실증 장비 신청 상세 페이지용 생성자
	public DemonstrationListDTO(Long demNum, String demName, String demInfo, Long itemNum, LocalDate expDate,String imageName,String imageUrl) {
		this.demNum = demNum;
		this.demName = demName;
		this.demInfo = demInfo;
		this.itemNum = itemNum;
		this.expDate = expDate;
		this.imageName=imageName;
		this.imageUrl=imageUrl;
	}

	// 물품 대여 조회 페이지용 생성자
	public DemonstrationListDTO(Long demNum, String demName, String demMfr, Long itemNum, LocalDate startDate,
			LocalDate endDate, LocalDate applyAt) {
		this.demNum = demNum;
		this.demName = demName;
		this.demMfr = demMfr;
		this.itemNum = itemNum;
		this.startDate = startDate;
		this.endDate = endDate;
		this.applyAt = applyAt;
	}
}
