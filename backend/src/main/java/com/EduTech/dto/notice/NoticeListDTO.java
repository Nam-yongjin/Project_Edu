package com.EduTech.dto.notice;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NoticeListDTO { //목록조회용
	
	private Long noticeNum; //공지사항번호
	
	private String title; //제목
	
	private boolean isPinned; //게시판 고정
	
	private String name; //이름
	
	private LocalDateTime createdAt; //작성일
	
	private Long view; //조회수

}
