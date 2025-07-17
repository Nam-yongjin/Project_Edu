package com.EduTech.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyModifyDTO extends MemberModifyDTO{

	@NotBlank(message = "기업명은 필수입니다.")
	private String companyName;
	
	@NotBlank(message = "직급은 필수입니다.")
	private String position;
}
