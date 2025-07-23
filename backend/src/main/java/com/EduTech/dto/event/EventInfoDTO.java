package com.EduTech.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.event.RevState;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // 기본 생성자 (ModelMapper가 필요로 함)
@AllArgsConstructor // 모든 필드를 초기화하는 생성자 (Builder와 함께 권장)
public class EventInfoDTO {
    
    private Long eventNum;			// 행사 아이디
    private String eventName;		// 행사명
    private String eventInfo;		// 소개
    private EventState state; 		// 신청전, 신청중, 신청마감
    private RevState revState;		// 상태(대기, 수락, 거절)
    private String target;			// 모집대상
    private EventCategory category; // 모집대상 분류
    private Integer maxCapacity;	// 모집인원
    private Integer currCapacity;	// 현재인원
    private String place;			// 장소
    private String etc;				// 기타 유의사항

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime applyStartPeriod;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime applyEndPeriod;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime eventStartPeriod;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime eventEndPeriod;

    private String originalName;	// 대표 파일명
    private String filePath;		// 대표 파일 경로

    private List<Integer> daysOfWeek;
    private List<String> dayNames;
}
