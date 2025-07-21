package com.EduTech.dto.member;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // 부모 클래스 필드 포함하여 equals/hashCode 생성
public class StudentDetailDTO extends MemberDetailDTO{

	private String schoolName;	//학교
}
