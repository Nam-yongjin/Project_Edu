package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;


@Data
public class DemonstrationResRentalDTO { // 추후 구현 예정 (물품 대여 조회 페이지에서 연기신청, 반납 조기 신청 받아올거) (프론트->백)
	private List<Long> demRevNum; // 실증 신청 번호
	private LocalDate updatedStartDate; // 변경될 시작 날짜
	private LocalDate updatedEndDate; // 변경될 끝 날짜
	private LocalDate originStartDate; // 기존 시작 날짜
	private LocalDate originEndDate; // 기존 끝 날짜
	private Long demNum; // 실증 번호
	private String memId; // 사용자 아이디
}
