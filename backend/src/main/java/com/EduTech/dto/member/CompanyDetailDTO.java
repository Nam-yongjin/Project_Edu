package com.EduTech.dto.member;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // 부모 클래스 필드 포함하여 equals/hashCode 생성
public class CompanyDetailDTO extends MemberDetailDTO{
	
	private String companyName;	// 회사명
	
	private String position;	// 직책명
}
