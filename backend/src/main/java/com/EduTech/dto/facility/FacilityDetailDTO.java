package com.EduTech.dto.facility;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/* 시설 상세 조회용 복합 DTO */
@Getter
@Setter
public class FacilityDetailDTO {
    private Long facRevNum;         // 추가 권장: 장소 식별자
    private String facName;
    private int capacity;
    private String facItem;
    private String etc;
    private String availableTime;
    private String facInfo;
    private List<FacilityImageDTO> images;
}
