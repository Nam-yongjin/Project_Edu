package com.EduTech.dto.facility;

import java.time.LocalTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FacilityUpdateRequestDTO {
    private Long facRevNum;

    private String facName;
    private String facInfo;
    private Integer capacity;
    private String facItem;
    private String etc;

    private LocalTime reserveStart;
    private LocalTime reserveEnd;

    /** 삭제할 이미지 PK 목록 */
    private List<Long> removeImageIds;
}
