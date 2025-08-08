package com.EduTech.dto.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFileDTO {
	
    private Long id;
    
    private String filePath;
    
    private String originalName;
    
}