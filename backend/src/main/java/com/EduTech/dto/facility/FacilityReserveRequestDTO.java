package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 시설 예약 요청 DTO (프론트에서 예약 요청 시 사용) */
@Data
@NoArgsConstructor             // ← 추가
@AllArgsConstructor            // ← 추가
@Builder
public class FacilityReserveRequestDTO {
    private Long facRevNum;       // 시설 PK (서비스는 이 값만 사용 권장)

    // 선택(표시/호환 목적): 서비스 로직에서는 사용하지 않기를 권장
    private String facName;
    private String memId;

    private LocalDate facDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
}
