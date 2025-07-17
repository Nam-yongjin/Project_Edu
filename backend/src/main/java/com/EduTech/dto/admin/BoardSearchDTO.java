package com.EduTech.dto.admin;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BoardSearchDTO {
	private String boardType; //게시판 유형

    private String option; //검색 대상(title, content, writer)	
      
    private String searchKeyword; //검색어(제목/내용)에 포함된 단어	
  
    private LocalDate startDate; //작성 시작일  
 
    private LocalDate endDate; //작성 종료일   

    private Boolean showPinnedOnly; //고정된 글만 보기

    private Boolean showUnpinnedOnly; //일반 글만 보기
    
    private String sortBy; //정렬 기준

    private String orderBy; //정렬 순서
}
