package com.EduTech.dto.notice;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class NoticeDetailDTO { //상세페이지
	
	private Long noticeNum; //공지사항 번호 (기본 키)
	
	private String title; //제목
	
	private String content; //내용
	
	private Boolean isPinned; //게시판 고정
	
	private String name; //이름
	
	private String mem_id; //회원id
	
	private Long viewCount; //조회수
	
	private LocalDateTime createdAt; //작성일
	
	private LocalDateTime updatedAt; //수정일
	
	private List<NoticeFileDTO> files; //첨부파일 목록
	

}
