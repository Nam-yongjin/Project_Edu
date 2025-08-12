package com.EduTech.dto.facility;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservedBlockDTO {
	
    private LocalTime start;
    
    private LocalTime end;
    
}
