package com.EduTech.dto.news;

import lombok.Data;

@Data
public class NewsSearchDTO {
	
	private int page; //현재 페이지
	
	private int size; //한 페이지 당 게시글 수
	
	private String keyword; //검색어(제목/내용)에 포한된 단어
	
	private String searchType; //검색 대상(title, content, writer)
	
	private String name; //작성자
	
	private String sort; //정렬 기준
	
	private String orderBy; //정렬 순서

}
