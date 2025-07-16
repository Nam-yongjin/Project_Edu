package com.EduTech.dto.news;

import java.util.List;

import lombok.Data;

@Data
public class NewsDTO {
	
	private String title; //제목
	
	private String content; //내용
	
	private String memId; //회원아이디(작성자 식별)
	
	private List<String> urlList; //첨부된 파일 URL목록

}
