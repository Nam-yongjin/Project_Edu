package com.EduTech.dto.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
	private Long evRevNum;	//행사신천번호
	private LocalDateTime applyAt;	// 행사신청일
}
