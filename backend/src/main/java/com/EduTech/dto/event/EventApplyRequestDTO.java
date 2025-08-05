package com.EduTech.dto.event;

import com.EduTech.entity.event.RevState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventApplyRequestDTO {
	private Long eventNum;
	private String memId;
	
	@Builder.Default
	private RevState revState = RevState.APPROVED;
}
