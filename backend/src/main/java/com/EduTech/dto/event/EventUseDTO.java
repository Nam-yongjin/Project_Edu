package com.EduTech.dto.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUseDTO {
	private Long evRevNum;					// 행사신청번호
	private LocalDateTime applyAt;			// 행사신청일
	private String eventName;				// 행사명
	private LocalDateTime applyStartyPeriod;// 신청시작기간
	private LocalDateTime applyEndPeriod;	// 신청종료기간
	
	private LocalDateTime progressStartPeriod;	// 진행시작기간
	private LocalDateTime progressEndPeriod;	// 진행종료기간
	
	private String place;					// 장소
	private Integer totCapacity;			// 모집인원
	private Integer currCapacity;			// 현재인원
	private Boolean state;					// 신청상태
	
	private Long eventNum;	// 행사 아이디
	private String memId;	// 회원 아이디
	private String name;	// 회원 이름
	private String email;	// 회원 이메일
	private String phone;	// 회원 전화번호
}
