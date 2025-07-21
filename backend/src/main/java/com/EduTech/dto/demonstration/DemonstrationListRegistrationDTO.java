package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DemonstrationListRegistrationDTO { // 기업 실증 조회에 사용하는 dto (백->프톤트)

	private Long demRegNum; // demonstration_registration의 기본키
	private LocalDate regDate; // 등록 일자를 현재 시간으로 설정
	private LocalDate expDate; // 반납 예정 일자
	private DemonstrationState state = DemonstrationState.WAIT; // 상태의 디폴트 값을 WAIT 상태로 저장
	private String memId; // 회원 아이디
	private String companyName; // 기업명
	private String position; // 직급

	public DemonstrationListRegistrationDTO(Long demRegNum, LocalDate regDate, LocalDate expDate,
			DemonstrationState state, String memId, String companyName, String position) {
		this.demRegNum = demRegNum;
		this.regDate = regDate;
		this.expDate = expDate;
		this.state = state;
		this.memId = memId;
		this.companyName = companyName;
		this.position = position;
	}
	
	

}