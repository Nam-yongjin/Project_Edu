package com.EduTech.dto.facility;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 시설 상세 조회용 복합 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDetailDTO {

    private FacilityDTO facility;                	// 시설 정보
    private List<FacilityImageDTO> images;          // 이미지 목록
    private List<FacilityTimeDTO> availableTimes;   // 예약 가능 시간 목록
    
}