package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DemonstrationSelectDTO { // 실증 장비 목록 페이지 데이터 받는 dto (상세아님)
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
}
