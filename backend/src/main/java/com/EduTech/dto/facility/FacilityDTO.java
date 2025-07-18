package com.EduTech.dto.facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDTO {

    private Long facilityNum;	// 시설 번호
    private String facName;     // 시설명
    private String facInfo;     // 소개
    private String category;    // 분류 (CONFERENCE, SEMINAR)
    private int capacity;       // 수용 인원
    private String facItem;     // 구비 품목
    private String etc;         // 기타 유의사항
    
}
