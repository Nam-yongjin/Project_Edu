package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemonstrationBorrowResListDTO {
	private LocalDate applyAt; 
	private LocalDate startDate;
	private LocalDate endDate;
	private DemonstrationState state = DemonstrationState.WAIT; // 디폴트 값을 WAIT로 저장 (현재 상태: 대기, 수락, 거부)
	private String memId; // 회원 아이디
	private String schoolName; // 학교 이름
	private String demName;
	private String addr; 
	private String phone; 
	private Long bItemNum;

	public DemonstrationBorrowResListDTO(LocalDate applyAt, DemonstrationState state, String memId, String schoolName)
	{
		this.applyAt=applyAt;
		this.state=state;
		this.memId=memId;
		this.schoolName=schoolName;
	}
}
