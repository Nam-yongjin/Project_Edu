package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DemonstrationTimeDTO { // 이 dto는 실증 신청의 사용 시작일 ~ 사용 종료일을 하루 단위로 세세하기 나눈 dto를 뜻함. 
		private LocalDate demDate; // 사용 시작일과 사용 종료일을 하루 단위로 나눈 날짜
		private Boolean state; // 예약 상태
		private Long demNum; // 실증 번호
}
