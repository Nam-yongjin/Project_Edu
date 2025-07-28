package com.EduTech.dto.facility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityImageDTO {
    private Long facImageNum;  // 이미지 고유 번호.
    private String imageName;  // 이미지 원본 이름
    private String imageUrl;   // 이미지 접근 경로
}