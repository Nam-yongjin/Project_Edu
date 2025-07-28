package com.EduTech.dto.facility;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/* 시설 상세 조회용 복합 DTO */
@Getter
@Setter
public class FacilityDetailDTO {
    private String facName;			// 장소명.
    private int capacity;			// 수용인원
    private String facItem;			// 구비품목
    private String etc;				// 기타 유의사항
    private String availableTime;	// 에약 가능 시간
    private String facInfo;       	// 시설 소개

    private List<FacilityImageDTO> images;  // 이미지 정보 리스트
}