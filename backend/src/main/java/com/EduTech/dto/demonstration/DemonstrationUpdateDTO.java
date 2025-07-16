package com.EduTech.dto.demonstration;

import lombok.Data;

@Data
public class DemonstrationUpdateDTO {
	private String demName; // 물품명
	private String demInfo; // 믈품소개
	private String demMfr; // 제조사
	private Long itemNum; // 개수
}
