package com.EduTech.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class eventApplyRequestDTO {
	private Long eventNum;
	private String memId;
}
