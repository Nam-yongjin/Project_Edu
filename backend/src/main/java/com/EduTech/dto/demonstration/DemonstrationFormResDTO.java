package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationCategory;

import lombok.Data;

@Data // @Data 어노테이션 사용해서 getter, setter 메소드 생략

public class DemonstrationFormResDTO { // 기업이 실증 물품 dto (프론트->백)

	private String demName; // 물품명
	private String demInfo; // 믈품소개
	private String demMfr; // 제조사
	private Long itemNum; // 개수
	private LocalDate expDate; // 반납 예정일
	private Long demNum; // 실증 번호
	private String memId; // 회원 아이디
	private DemonstrationCategory category; // 카테고리
	private List<String> imageUrlList = new ArrayList<>(); // 저장할 이미지 리스트
	private List<String> imageNameList=new ArrayList<>(); // 저장할 이미지 이름 리스트
	private List<String> isMain=new ArrayList<>(); // 이미지 대표 여부 리스트
}