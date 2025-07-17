package com.EduTech.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberFindPwDTO {

	@NotBlank(message = "아이디를 입력하세요")
	private String memId;
	
	@NotBlank(message = "휴대폰 번호를 입력하세요")
	private String phone;
	
}
