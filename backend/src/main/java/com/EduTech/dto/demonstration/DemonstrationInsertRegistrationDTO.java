package com.EduTech.dto.demonstration;

import java.time.LocalDateTime;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.Data;

@Data 
public class DemonstrationInsertRegistrationDTO { // 상품 등록을 할때 사용하는 DTO
		private LocalDateTime regDate=LocalDateTime.now(); // 등록 일자를 현재 시간으로 설정
		private LocalDateTime expDate; // 반납 예정 일자 
		private DemonstrationState state=DemonstrationState.WAIT; // 상태의 디폴트 값을 WAIT 상태로 저장
		private DemonstrationInsertDTO demonstrationDto; // 상품 등록을 함으로써 실증 등록도 되므로 상품을 저장한 DTO
		private long demNum; // 실증 번호(상품 번호라 생각하기) 
		private String memId; // 회원 아이디
}
	