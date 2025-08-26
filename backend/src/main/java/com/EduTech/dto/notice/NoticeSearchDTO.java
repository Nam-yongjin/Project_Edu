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

	private Boolean isPinned; //게시판 고정(null값을 받을 수 있도록)
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate; //검색 기간 설정
	    
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

//프론트에서 GET /NoticeList?page=1&size=10&keyword=공지...(생략)&startDate=2025-08-01&endDate=2025-08-31 이렇게 요청을 보내면
// 스프링이 자동으로 매핑해서 searchDTO.getPage(); searchDTO.getSize(); 이렇게 값이 들어옴
//프론트 axios에서 params 객체를 정의해서 get에 넣으면 axios가 알아서 GET /NoticeList?page=1&size=10&keyword=공지...(생략)&startDate=2025-08-01&endDate=2025-08-31로 변환
}
