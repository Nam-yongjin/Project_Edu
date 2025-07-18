package com.EduTech.dto.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventInfo {
	
	private Long eventNum;		// 행사 아이디
	private String eventName;	// 행사명
	private String eventInfo;	// 소개
	private boolean state;		// 상태
	private String target;		// 모집대상
	private int totCapacity;	// 모집인원
	private int currCapacity;	// 현재인원
	private String place;		// 장소
	private String etc;			// 기타 유의상황
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime applyStartyPeriod; // 신청시작기간

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime applyEndPeriod; // 신청종료기간
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDate progressStartPeriod; // 수강시작날짜
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDate progressEndPeriod; // 수강종료날짜
	
	private String originalName;	// 원본파일명
	private String filePath;		// 파일저장경로
	
}
