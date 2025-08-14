package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.EduTech.entity.facility.FacilityState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityReserveListDTO {
    private Long reserveId;     // 예약 번호 (PK)
    private Long facRevNum;     // 시설 번호(옵션)
    private String facName;
    private LocalDate facDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private FacilityState state;
    private String mainImageUrl;
}
