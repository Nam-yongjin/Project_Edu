package com.EduTech.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventApplyRequestDTO {
	private Long eventNum;	// 행사 아이디
	private String memId;	// 회원 아이디
}
