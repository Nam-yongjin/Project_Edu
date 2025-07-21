package com.EduTech.dto.event;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.EduTech.entity.event.EventState;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventBannerDTO {
	
	private Long evtFileName;		// 첨부파일 번호
	private String originalName;	// 원본파일명
	private String filePath;		// 파일저장경로
	private EventState state;		// 현재 신청 가능 여부
	private String thumbnailPath;	// 썸네일
	
	private String progName;		// 행사명
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDateTime applyStartyPeriod; // 신청시작기간(년 일 월)

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDateTime applyEndPeriod; 	// 신청종료기간
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	private LocalTime startTime; // 수강시작시간(시 분)
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	private LocalTime endTime; // 수강종료시간
	
	private Integer totCapacity;	// 모집인원
}
