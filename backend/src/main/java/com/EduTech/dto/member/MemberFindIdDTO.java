package com.EduTech.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberFindIdDTO {

	@NotBlank(message = "이름을 입력하세요")
	private String name;
	
	@NotBlank(message = "휴대폰 번호를 입력하세요")
	private String phone;
}
