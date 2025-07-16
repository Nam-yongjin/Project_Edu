package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.Data;


@Data
public class DemonstrationUpdateReserveDTO {
	private Long demRevNum; // demonstration_reserve 테이블의 기본키
	private LocalDate startDate; // 제품 제공 받을 날짜
	private LocalDate endDate;  // 대여 기간 끝나는 날짜
	private DemonstrationState state= DemonstrationState.WAIT; // 디폴트 값을 WAIT로 저장 (현재 상태: 대기, 수락, 거부)
	private Long demNum; // 실증 번호(상품 번호라 생각하기) 
	private String memId; // 회원 아이디
}
