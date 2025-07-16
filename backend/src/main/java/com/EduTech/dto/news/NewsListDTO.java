package com.EduTech.dto.news;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NewsListDTO {
	
	private Long noticeNum; //공지사항번호
	
	private String title; //제목
	
	private String name; //이름
	
	private LocalDateTime createdAt; //작성일
	
	private Long view; //조회수

}
