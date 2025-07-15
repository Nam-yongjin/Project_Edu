package com.EduTech.dto.demonstration;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class DemonstrationImageDTO { // 제품 이미지 등록 DTO
	private String imageName; // 이미지 이름
	private String imageUrl; // 이미지 url
	private long demNum; // 실증 번호 (실증 번호 등록시에는 먼저 실증 제품을 save하고 demNum을 값을 받아와서 set 후 이미지를 저장하면 될듯)
}
