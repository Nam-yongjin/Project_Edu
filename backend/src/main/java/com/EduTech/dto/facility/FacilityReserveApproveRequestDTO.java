package com.EduTech.dto.facility;

import com.EduTech.entity.facility.FacilityState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityReserveApproveRequestDTO {
    private Long reserveId;          // 예약 번호 (PK)
    
    private FacilityState state;     // ACCEPT / REJECT
}