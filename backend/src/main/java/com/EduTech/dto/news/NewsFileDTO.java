package com.EduTech.dto.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsFileDTO {
	
	private String originalName; //원본파일명
	
	private String filePath; //파일경로
	
	private String fileType; //파일종류

}
