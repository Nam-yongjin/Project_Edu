package com.EduTech.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventBannerDTO {
	
	private Long evtFileNum;		// 첨부파일 번호
	private String originalName;	// 원본파일명
	private String filePath;		// 파일저장경로
	private String fileType;		// 파일 종류
	private String thumbnailPath;	// 썸네일 경로
	private Long eventInfoId;		// 
	
	private String eventName;		// 행사명
	private String target;			// 모집 대상
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime eventStartPeriod; // 행사시작기간(년 일 월)

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime eventEndPeriod; 	// 행사종료기간
	
	private List<Integer> daysOfWeek; // 요일 (숫자)
	private List<String> dayNames;    // 요일 한글명 (월, 화, 수...)
}
