package com.EduTech.service.demonstration;

import java.util.List;

import com.EduTech.dto.demonstration.DemonstrationFormDTO;
import com.EduTech.dto.demonstration.DemonstrationListDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationRegApporvalDTO;
import com.EduTech.dto.demonstration.DemonstrationResApporvalDTO;
import com.EduTech.dto.demonstration.DemonstrationResRentalDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationCancelDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeDTO;


public interface DemonstrationService {
	
		List<DemonstrationListReserveDTO> findAllDemRes(String searchText); // 실증 교사 신청목록 조회 기능 (검색도 같이 구현할 것임.)
		List<DemonstrationListRegistrationDTO> findAllDemReg(String searachText); // 실증 기업 신청목록 조회 가능(검색도 같이 구현할 것임.)
		void approveOrRejectDemRes(DemonstrationResApporvalDTO demonstrationResApporvalDTO); // 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
		void approveOrRejectDemReg(DemonstrationRegApporvalDTO demonstrationRegApporvalDTO); // 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
		List<DemonstrationListDTO> findAllDemRental(String memId); // 회원이 신청한 물품 대여 조회 페이지 조회 기능 (검색도 같이 구현할 것임.)
		void rentalDateChange(DemonstrationResRentalDTO demonstrationResRentalDTO); // 물품 대여 조회 페이지 연기 신청 및 반납 조기 신청
		List<DemonstrationListDTO> findAllDemList(); // 실증 장비신청 페이지 (실증 물품 리스트 목록)
		List<DemonstrationTimeDTO> checkReservationState(Long demNum); // 해당 상품이 예약 상태인지 확인 가능(실증 장비 신청 페이지에서 대여가능 / 예약 마감 표기 할거임)
		List<DemonstrationListDTO> findDemDetailList(Long demNum); // 실증 장비 신청 상세 페이지
		void demonstrationReservation(DemonstrationReservationDTO demonstrationReservationDTO); // 실증 신청 상세 페이지에서 예약 신청하기 클릭시, 예약 정보 저장
		void demonstrationReservationCancel(DemonstrationReservationCancelDTO demonstrationReservationCancelDTO); // 실증 신청 상세 페이지에서 예약 취소하기 클릭 시, 예약 정보 취소
		void addDemonstration(DemonstrationFormDTO demonstrationFormDTO); // 실증 상품 등록 페이지에서 실증 상품 등록하는 기능
}