package com.EduTech.dto.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.EduTech.entity.event.EventCategory;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventInfoDTO {
	
	private Long eventNum;			// 행사 아이디
	private String eventName;		// 행사명
	private String eventInfo;		// 소개
	private String revState;		// 상태(대기, 수락, 거절)  현제 serviceimpl의 관리자 일반목록 검색으로 인해 String로 바꿈 하지만 나중에 RevState로 바꿔야함
	private String target;			// 모집대상
	private EventCategory category; // 모집대상 분류 (일반인, 학생, 선생)
	private Integer maxCapacity;	// 모집인원
	private Integer currCapacity;	// 현재인원
	private String place;			// 장소
	private String etc;				// 기타 유의상황
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime applyStartPeriod; // 신청시작기간(년 일 월 시 분)

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime applyEndPeriod; // 신청종료기간
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate eventStartPeriod; // 행사시작날짜(년 일 월)
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate eventEndPeriod; // 행사종료날짜
	
	private String originalName;	// 대표 원본 파일명
	private String filePath;		// 대표 파일 저장경로
	
}
