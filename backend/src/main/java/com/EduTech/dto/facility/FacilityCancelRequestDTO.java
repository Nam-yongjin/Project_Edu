package com.EduTech.dto.facility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityCancelRequestDTO {
    private Long reserveId;
    private String memId; // JWT에서 읽으면 제거 가능
}
