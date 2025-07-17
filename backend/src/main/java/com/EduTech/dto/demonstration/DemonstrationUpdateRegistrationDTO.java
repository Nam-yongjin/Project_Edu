package com.EduTech.dto.demonstration;

import java.time.LocalDate;

import com.EduTech.entity.demonstration.DemonstrationState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DemonstrationUpdateRegistrationDTO {

	private Long demRegNum; // demonstration_registration의 기본키
	private LocalDate expDate; // 반납 예정 일자
	private DemonstrationState state = DemonstrationState.WAIT; // 상태의 디폴트 값을 WAIT 상태로 저장
	private DemonstrationFormDTO demonstrationDto; // 상품 등록을 함으로써 실증 등록도 되므로 상품을 저장한 DTO
	private Long demNum; // 실증 번호(상품 번호라 생각하기)
	private String memId; // 회원 아이디
}
