package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemonstrationListRegistrationDTO { // 관리자 실증 신청 내역 조회

	private Long demRegNum; // demonstration_registration의 기본키
	private Long demNum;
	private LocalDate regDate; // 등록 일자를 현재 시간으로 설정
	private LocalDate expDate; // 반납 예정 일자
	private DemonstrationState state; // 상태의 디폴트 값을 WAIT 상태로 저장
	private String memId; // 회원 아이디
	private String companyName; // 기업명
	private String demName;
	private String addr; 
	private String addrDetail;
	private String phone; 
	private Long itemNum;
	//private List<DemonstrationImageDTO> imageList=new ArrayList<>(); 
	

}