package com.EduTech.dto.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.EduTech.entity.event.RevState;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventInfoDTO {
	
	private Long eventNum;			// 행사 아이디
	private String eventName;		// 행사명
	private String eventInfo;		// 소개
	private RevState revState;		// 상태(대기, 수락, 거절)
	private String target;			// 모집대상
	private Integer totCapacity;	// 모집인원
	private Integer currCapacity;	// 현재인원
	private String place;			// 장소
	private String etc;				// 기타 유의상황
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime applyStartPeriod; // 신청시작기간(년 일 월 시 분)

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime applyEndPeriod; // 신청종료기간
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate progressStartPeriod; // 수강시작날짜(년 일 월)
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate progressEndPeriod; // 수강종료날짜
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	private LocalTime startTime; // 수강시작시간(시 분)

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	private LocalTime endTime; // 수강종료시간
	
	private String originalName;	// 대표 원본 파일명
	private String filePath;		// 대표 파일 저장경로
	
	private List<Integer> daysOfWeek; // 요일 (숫자)
	private List<String> dayNames;
	
}
