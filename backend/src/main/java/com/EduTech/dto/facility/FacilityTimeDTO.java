package com.EduTech.dto.facility;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FacilityTimeDTO {
	
    private LocalDate facDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private boolean available;
    
}
