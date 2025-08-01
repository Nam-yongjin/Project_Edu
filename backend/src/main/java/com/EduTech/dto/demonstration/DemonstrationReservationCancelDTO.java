package com.EduTech.dto.demonstration;

import lombok.Data;

@Data
public class DemonstrationReservationCancelDTO { // 실증 신청 상세페이지에서 예약 취소 버튼 클릭 시 전달되는 dto (프론트->백)
	private Long demNum; // 실증 상품 번호
	private String memId; // 신청자 아이디
}
