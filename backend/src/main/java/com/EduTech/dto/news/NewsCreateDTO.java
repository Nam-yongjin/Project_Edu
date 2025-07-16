package com.EduTech.dto.news;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class NewsCreateDTO {
	
	private String title; //제목
	
	private String content; //내용
	
	private String name; //이름
	
	private String memId; //아이디
	
	private List<MultipartFile> files; //첨부할 파일

}
