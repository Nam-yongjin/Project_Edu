package com.EduTech.dto.facility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityCancelRequestDTO {
    private Long facRevNum;    // 예약 번호
    private boolean isAdmin;   // 관리자 여부 (true 시 관리자로 강제 취소)
    private String memId;      // 사용자 식별자 (사용자 취소 시 검증용)
}
