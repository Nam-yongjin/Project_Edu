package com.EduTech.dto.facility;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 시설 예약 요청 DTO (프론트에서 예약 요청 시 사용) */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityReserveRequestDTO {

    private Long facilityNum;        // 예약할 시설 번호
    private String memId;            // 예약자 아이디
    private LocalDateTime startDate; // 이용 시작 일시
    private LocalDateTime endDate;   // 이용 종료 일시
    
}