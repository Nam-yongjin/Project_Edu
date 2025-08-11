package com.EduTech.dto.facility;

import java.util.List;

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
public class FacilityListDTO {

	private Long facRevNum;
	
	private String facName;
	
	private String facInfo;
	
    private int capacity;
	
    private List<String> images;
    
}
