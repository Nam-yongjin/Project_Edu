package com.EduTech.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import com.EduTech.entity.event.EventCategory;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventBannerDTO {
	
	private Long bannerNum;		// 배너 번호
	private String originalName;	// 원본파일명
	private String filePath;		// 파일저장경로
	
	// private String fileType;		// 파일 종류 사용하지 않으므로 제거
	// private String thumbnailPath;	// 썸네일 경로 사용하지 않으므로 제거
	
	private Long eventNum;		// 행사 ID
	
	private String eventName;		// 행사명
	private EventCategory category;	// 모집 대상
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime eventStartPeriod; // 행사시작기간(년 일 월)

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime eventEndPeriod; 	// 행사종료기간

}
