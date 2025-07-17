package com.EduTech.dto.admin;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BoardListDTO {
	 private Long Boardnum; //게시판 글 번호
		
	    private String title; //제목
		
	    private boolean isPinned; //게시판 고정
		
	    private String name; //이름

	    private String writerid; //작성자id
		
	    private LocalDateTime createdAt; //작성일

	    private LocalDateTime updatedAt; //수정일
		
	    private Long view; //조회수
}
