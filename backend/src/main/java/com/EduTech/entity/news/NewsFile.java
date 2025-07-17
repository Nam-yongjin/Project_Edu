package com.EduTech.entity.news;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news_file")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewsFile {

	@Id
	private Long newsFileNum; //첨부파일번호
	
	private String originalName; //원본파일명
	
	private String fileUrl; //파일URL 저장경로
	
	private String filePath; //파일저장경로

	private String fileType; //파일종류
	
	//여러 개의 파일을 하나의 기사에 첨부
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "newsNum", nullable = false)
	private News news; //뉴스번호
}
