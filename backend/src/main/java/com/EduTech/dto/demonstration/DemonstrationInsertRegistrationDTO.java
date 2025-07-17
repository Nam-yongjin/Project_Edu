package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import com.EduTech.entity.demonstration.DemonstrationState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DemonstrationInsertRegistrationDTO { // 상품 등록을 할때 사용하는 DTO

	@NotNull
	private LocalDate regDate = LocalDate.now(); // 등록 일자를 현재 시간으로 설정

	@NotNull
	private LocalDate expDate; // 반납 예정 일자

	@NotNull
	private DemonstrationState state = DemonstrationState.WAIT; // 상태의 디폴트 값을 WAIT 상태로 저장

	@NotNull
	private Long demNum; // 실증 번호(상품 번호라 생각하기)

	@NotBlank
	private String memId; // 회원 아이디
}
