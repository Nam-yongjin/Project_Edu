package com.EduTech.dto.demonstration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // 리포지토리 자체에서 객체 생성 후, 리스트에 담을 예정이라 생성자 설정을 하였음.
@NoArgsConstructor
public class DemonstrationImageDTO { // 제품 이미지 등록 DTO
	private String imageName; // 이미지 이름
	private String imageUrl; // 이미지 url
	private Long demNum; // 실증 번호 (실증 번호 등록시에는 먼저 실증 제품을 save하고 demNum을 값을 받아와서 set 후 이미지를 저장하면 될듯)
}
