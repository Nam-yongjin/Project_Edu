package com.EduTech.dto.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventBannerDTO {
	
	private Long evtFileName;		// 첨부파일 번호
	private String originalName;	// 원본파일명
	private String filePath;		// 파일저장경로
	private String thumbnailPath;	// 썸네일
	
	private String progName;		// 행사명
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime applyStartyPeriod; // 신청시작기간

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime applyEndPeriod; 	// 신청종료기간
	
	private Integer totCapacity;	// 모집인원
}
