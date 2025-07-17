package com.EduTech.dto.demonstration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DemonstrationUpdateDTO {

	@NotBlank
	private String demName; // 물품명

	@NotBlank
	private String demInfo; // 믈품소개

	@NotBlank
	private String demMfr; // 제조사

	@NotNull
	private Long itemNum; // 개수
}
