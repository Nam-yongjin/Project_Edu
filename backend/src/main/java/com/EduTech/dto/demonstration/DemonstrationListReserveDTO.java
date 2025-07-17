package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.Data;

@Data
public class DemonstrationListReserveDTO { // 실증 교사 신청 조회하는 dto

	private Long demRevNum; // demonstration_reserve 테이블의 기본키
	private LocalDate applyAt; // 현재 시간 값을 가져옴 (등록일)
	private DemonstrationState state = DemonstrationState.WAIT; // 디폴트 값을 WAIT로 저장 (현재 상태: 대기, 수락, 거부)
	private String memId; // 회원 아이디
	private String schoolName;
	

	public DemonstrationListReserveDTO(Long demRevNum, LocalDate applyAt, DemonstrationState state, String memId, String schoolName)
	{
		this.demRevNum=demRevNum;
		this.applyAt=applyAt;
		this.state=state;
		this.memId=memId;
		this.schoolName=schoolName;
	}
}
