package com.EduTech.dto.news;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class NewsUpdateRegisterDTO {
	
	private String title; //제목
	
	private String content; //내용
	
	private List<String> oldFiles; //기존 파일목록
	
	private List<MultipartFile> newFiles; //새로 업로드 할 파일

}
