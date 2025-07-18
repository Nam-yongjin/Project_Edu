package com.EduTech.service.demonstration;

import java.util.List;

import com.EduTech.dto.demonstration.DemonstrationListDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationRegApporvalDTO;
import com.EduTech.dto.demonstration.DemonstrationResApporvalDTO;
import com.EduTech.dto.demonstration.DemonstrationResRentalDTO;


public interface DemonstrationService {
	
		List<DemonstrationListReserveDTO> findAllDemRes(); // 실증 교사 신청목록 조회 기능 (검색도 같이 구현할 것임.)
		List<DemonstrationListRegistrationDTO> findAllDemReg(); // 실증 기업 신청목록 조회 가능(검색도 같이 구현할 것임.)
		void approveOrRejectDemRes(DemonstrationResApporvalDTO demonstrationResApporvalDTO); // 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
		void approveOrRejectDemReg(DemonstrationRegApporvalDTO demonstrationRegApporvalDTO); // 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
		List<DemonstrationListDTO> findAllDemRental(); // 회원이 신청한 물품 대여 조회 페이지 조회 기능 (검색도 같이 구현할 것임.)
		void RentalDateChange(DemonstrationResRentalDTO demonstrationResRentalDTO); // 물품 대여 조회 페이지 연기 신청 및 반납 조기 신청
		List<DemonstrationListDTO> findAllDemList();
		
		
		
		
		
		
	
}