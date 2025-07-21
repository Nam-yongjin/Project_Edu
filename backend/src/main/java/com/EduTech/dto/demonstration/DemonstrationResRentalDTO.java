package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import lombok.Data;


@Data
public class DemonstrationResRentalDTO { // 추후 구현 예정 (물품 대여 조회 페이지에서 연기신청, 반납 조기 신청 받아올거) (프론트->백)
	private Long demRevNum;
	private Integer days;
	private LocalDate date;
}
