package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.EduTech.entity.facility.FacilityState;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityReserveDTO {
    private Long reserveId;        // 예약 PK
    private Long facRevNum;        // 시설 PK
    private String facName;        // 표시용
    private LocalDate facDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private FacilityState state;
    private LocalDateTime reserveAt;
}
