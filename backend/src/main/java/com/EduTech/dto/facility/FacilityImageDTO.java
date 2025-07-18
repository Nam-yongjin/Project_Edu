package com.EduTech.dto.facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityImageDTO {

    private Long facImageNum;   // 이미지 번호
    private String imageName;   // 이미지 이름
    private String imageUrl;    // 이미지 URL
    private Long facilityNum;   // 시설 번호
}