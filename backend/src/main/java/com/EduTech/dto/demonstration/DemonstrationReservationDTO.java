package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DemonstrationReservationDTO { // 실증 상품 상세 페이지에서 날짜 선택 후 예약 신청 버튼 클릭 시, 예약 정보를 받는 dto (프론트->백)
	private Long demNum; // 상품 번호
	private LocalDate startDate; // 시작 날짜
	private LocalDate endDate; // 끝 날짜
	private String memId; // 신청자 아이디
}
