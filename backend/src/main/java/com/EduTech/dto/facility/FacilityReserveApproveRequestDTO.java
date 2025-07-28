package com.EduTech.dto.facility;

import com.EduTech.entity.facility.FacilityState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityReserveApproveRequestDTO {
    private Long facRevNum;          // 예약 번호.
    private FacilityState state; // "ACCEPT" 또는 "REJECT"
}