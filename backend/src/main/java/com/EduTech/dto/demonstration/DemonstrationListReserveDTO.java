package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.Data;

@Data
public class DemonstrationListReserveDTO { // 실증 교사 신청 조회하는 dto (백->프론트)

	private Long demRevNum; // demonstration_reserve 테이블의 기본키
	private LocalDate applyAt; 
	private LocalDate startDate;
	private LocalDate endDate;
	private DemonstrationState state = DemonstrationState.WAIT; // 디폴트 값을 WAIT로 저장 (현재 상태: 대기, 수락, 거부)
	private String memId; // 회원 아이디
	private String schoolName; // 학교 이름
	private String addr; 
	private String phone; 
	private String demName;
	private Long bItemNum;
	private ResRequestDTO requqestDTO;
	
	

	public DemonstrationListReserveDTO(Long demRevNum, LocalDate applyAt, DemonstrationState state, String memId, String schoolName)
	{
		this.demRevNum=demRevNum;
		this.applyAt=applyAt;
		this.state=state;
		this.memId=memId;
		this.schoolName=schoolName;
	}
}
