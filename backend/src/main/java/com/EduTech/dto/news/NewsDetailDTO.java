package com.EduTech.dto.news;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsDetailDTO {
	
	private Long newsNum; //뉴스번호
	
	private String title; //제목
	
	private String content; //내용
	
	private String name; //이름
	
	private String mem_id; //회원id
	
	private Long viewCount; //조회수
	
	private LocalDateTime createdAt; //작성일
	
	private LocalDateTime updatedAt; //수정일
	
	private List<NewsFileDTO> files; //첨부파일목록

}
