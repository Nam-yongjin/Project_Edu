package com.EduTech.dto.demonstration;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DemonstrationSelectDTO {
	private long demNum; // 실증번호 (demonstration의 기본키)
	private String demName; // 물품명
	private String demInfo; // 믈품소개
	private String demMfr; // 제조사
	private long itemNum; // 개수
	private List<DemonstrationImageDTO> imageList = new ArrayList<>();	// 저장할 이미지 리스트
	private List<DemonstrationTimeDTO> demonstrationTimedDtoList= new ArrayList<>(); // 대여 가능한 날짜 dto
}
