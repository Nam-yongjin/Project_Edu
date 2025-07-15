package com.EduTech.dto.notice;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class NoticeDetailDTO { //상세페이지
	
	private String title; //제목
	
	private String content; //내용
	
	private boolean isPinned; //게시판 고정
	
	private String name; //이름
	
	private String writerMemid; //작성자id
	
	private Long view; //조회수
	
	private LocalDateTime createdAt; //작성일
	
	private LocalDateTime updatedAt; //수정일
	
	private List<NoticeFileDTO> fileDTO; //첨부파일목록
	

}
