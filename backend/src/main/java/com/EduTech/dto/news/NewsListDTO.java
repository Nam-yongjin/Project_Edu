package com.EduTech.dto.news;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsListDTO {
	
	private Long newsNum; //뉴스번호
	
	private String title; //제목
	
	private String name; //이름
	
	private LocalDateTime createdAt; //작성일
	
	private Long viewCount; //조회수
	
	private Integer fileCount; //파일 개수

}
