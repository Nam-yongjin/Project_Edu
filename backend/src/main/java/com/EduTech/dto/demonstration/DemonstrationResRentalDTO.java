package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import lombok.Data;


@Data
public class DemonstrationResRentalDTO { // 추후 구현 예정 (물품 대여 조회 페이지에서 연기신청, 반납 조기 신청 받아올거) (프론트->백)
	private Long demRevNum; // 실증 신청 번호
	private LocalDate updatedDate; // 변경될 날짜
	private LocalDate originDate; // 기존 날짜
	private Long demNum; // 실증 번호
}
