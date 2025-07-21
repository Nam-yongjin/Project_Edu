package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemonstrationTimeResDTO { // 이 dto는 실증 신청의 사용 시작일 ~ 사용 종료일을 하루 단위로 세세하기 나눈 dto를 뜻함. 
	// (백-> 클라이언트 받을때 사용)
	private LocalDate demDate; // 사용 시작일과 사용 종료일을 하루 단위로 나눈 날짜
	private Boolean state; // 예약 상태
	private Long demNum; // 실증 번호
}
