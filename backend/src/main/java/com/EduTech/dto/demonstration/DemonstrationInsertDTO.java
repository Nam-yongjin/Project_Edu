package com.EduTech.dto.demonstration;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data // @Data 어노테이션 사용해서 getter, setter 메소드 생략

public class DemonstrationInsertDTO { // 기업이 실증 물품 dto
	private String demName; // 물품명
	private String demInfo; // 믈품소개
	private String demMfr; // 제조사
	private Long itemNum; // 개수
	private List<DemonstrationImageDTO> imageList = new ArrayList<>();	// 저장할 이미지 리스트
	private List<DemonstrationTimeDTO> demonstrationTimedDtoList= new ArrayList<>(); // 대여 가능한 날짜 dto
}