package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import com.EduTech.entity.demonstration.DemonstrationState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DemonstrationInsertReserveDTO { // 실증 장비 신청 상세페이지에서 예약 신청하기 클릭시, 전달되는 dto

	@NotNull
	private LocalDate applyAt = LocalDate.now(); // 현재 시간 값을 가져옴 (등록일)

	@NotNull
	private LocalDate startDate; // 제품 제공 받을 날짜

	@NotNull
	private LocalDate endDate; // 대여 기간 끝나는 날짜

	@NotNull
	private DemonstrationState state = DemonstrationState.WAIT; // 디폴트 값을 WAIT로 저장 (현재 상태: 대기, 수락, 거부)

	@NotNull
	private Long demNum; // 실증 번호(상품 번호라 생각하기)

	@NotBlank
	private String memId; // 회원 아이디

}
