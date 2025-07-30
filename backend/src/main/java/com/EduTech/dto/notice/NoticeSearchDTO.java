package com.EduTech.dto.notice;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class NoticeSearchDTO { //검색
	
	private int page = 0; //현재 페이지
	
	private int size = 10; //한 페이지 당 게시글 수
	
	private String keyword; //검색어(제목/내용)에 포한된 단어
	
	private String searchType = "ALL"; //검색 대상(title, content, writer)
	
	private String name; //작성자
	
	private String sortBy = "createdAt"; //정렬 기준
	
	private String sortDirection = "DESC"; //정렬 순서

	private boolean isPinned; //게시판 고정
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate; //검색 기간 설정
	    
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;


}
