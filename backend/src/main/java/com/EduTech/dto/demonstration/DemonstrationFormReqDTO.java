package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.EduTech.entity.demonstration.DemonstrationCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // @Data 어노테이션 사용해서 getter, setter 메소드 생략

public class DemonstrationFormReqDTO { // 기업이 실증 물품 dto (프론트->백)

	@NotBlank(message = "물품명은 필수입니다.")
	@Size(max = 100, message = "물품명은 100자 이내여야 합니다.")
	private String demName; // 물품명

	@NotBlank(message = "물품 소개는 필수입니다.")
	@Size(max = 1000, message = "물품 소개는 1000자 이내여야 합니다.")
	private String demInfo; // 믈품소개

	@NotBlank(message = "제조사는 필수입니다.")
	@Size(max = 200, message = "제조사는 200자 이내여야 합니다.")
	private String demMfr; // 제조사

	@NotNull(message = "개수는 필수입니다.")
	@Min(value = 0, message = "0 이상의 값만 입력가능합니다.")
	private Long itemNum; // 개수
	
	@NotNull(message="반납 예정일은 필수입니다.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate expDate; // 반납 예정일
	
	@NotNull(message="카테고리 입력은 필수입니다.")
	private DemonstrationCategory category; // 카테고리
	
	private Long demNum; // 실증 번호
	private String memId; // 회원 아이디
	//@Size(max = 10, message = "이미지는 최대 10개까지 가능합니다.")
	//private List<MultipartFile> imageList = new ArrayList<>(); // 저장할 이미지 리스트
	private Integer mainImageIndex; // 이미지 대표 인덱스
	
}