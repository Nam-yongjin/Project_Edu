package com.EduTech.dto.news;

import lombok.Data;

@Data
public class NewsFileDTO {
	
	private String originalName; //원본파일명
	
	private String filePath; //파일경로
	
	private String fileType; //파일종류

}
