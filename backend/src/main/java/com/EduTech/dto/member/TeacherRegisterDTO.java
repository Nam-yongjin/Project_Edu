package com.EduTech.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // 부모 클래스 필드 포함하여 equals/hashCode 생성
public class TeacherRegisterDTO extends MemberRegisterDTO{

	@NotBlank(message = "학교명은 필수입니다.")
	private String schoolName;	//학교
}
