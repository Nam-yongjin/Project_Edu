package com.EduTech.dto.notice;

import lombok.Data;

@Data
public class NoticeFileDTO { //파일저장
		
	private String originalName; //원본파일명
	
	private String filePath; //파일경로
	
	private String fileType; //파일종류
		
}
