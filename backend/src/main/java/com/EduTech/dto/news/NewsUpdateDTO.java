package com.EduTech.dto.news;

import java.util.List;

import lombok.Data;

@Data
public class NewsUpdateDTO {
	
	private String title; //제목
	
	private String content; //내용
	
	private String name; //이름
	
	private String memId; //회원아이디
	
	private List<String> urlList; //새 파일
	
	private List<String> oldFiles; //남길 파일 목록

}
