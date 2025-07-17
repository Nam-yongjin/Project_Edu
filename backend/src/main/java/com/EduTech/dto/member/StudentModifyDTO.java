package com.EduTech.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentModifyDTO extends MemberModifyDTO{
	
	@NotBlank(message = "학교명은 필수입니다.")
	private String schoolName;
}
