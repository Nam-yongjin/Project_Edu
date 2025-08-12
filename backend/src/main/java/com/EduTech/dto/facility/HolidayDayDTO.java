package com.EduTech.dto.facility;

import java.time.LocalDate;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HolidayDayDTO {
    private LocalDate date; // 2025-07-10
    private String label;   // "설날" 또는 "정기점검" 등
    private String type;    // "PUBLIC" or "FACILITY"
}