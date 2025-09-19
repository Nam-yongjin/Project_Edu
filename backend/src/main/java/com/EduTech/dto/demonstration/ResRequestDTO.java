package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.RequestType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResRequestDTO {
	private Long demNum; // 실증 번호
	private RequestType type; // 요청 타입 ex) 반납/연기
	private DemonstrationState state; // 상태값
	private Long requestId; // 요청 번호
	private LocalDate updateDate; // 연장 날짜
	public ResRequestDTO(RequestType type,DemonstrationState state,LocalDate updateDate)
	{
		this.type=type;
		this.state=state;
		this.updateDate=updateDate;
	}
}
