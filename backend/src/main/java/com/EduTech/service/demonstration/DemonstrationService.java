package com.EduTech.service.demonstration;

import java.util.List;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.demonstration.DemonstrationDetailDTO;
import com.EduTech.dto.demonstration.DemonstrationFormDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationPageListDTO;
import com.EduTech.dto.demonstration.DemonstrationRentalListDTO;
import com.EduTech.dto.demonstration.DemonstrationResRentalDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationCancelDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeReqDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeResDTO;


public interface DemonstrationService {
	
		PageResponseDTO<DemonstrationListReserveDTO> getAllDemRes(String search,Integer pageCount); // 실증 교사 신청목록 조회 기능 (검색도 같이 구현할 것임.)
		PageResponseDTO<DemonstrationListRegistrationDTO> getAllDemReg(String search,Integer pageCount); // 실증 기업 신청목록 조회 가능(검색도 같이 구현할 것임.)
		PageResponseDTO<DemonstrationRentalListDTO> getAllDemRental(String memId, String search, Integer pageCount); // 회원이 신청한 물품 대여 조회 페이지 조회 기능 (검색도 같이 구현할 것임.)
		PageResponseDTO<DemonstrationPageListDTO> getAllDemList(Integer pageCount); // 실증 장비신청 페이지 (실증 물품 리스트 목록)
		DemonstrationDetailDTO getDemDetailList(Long demNum); // 실증 장비 신청 상세 페이지
		List<DemonstrationTimeResDTO> checkReservationState(DemonstrationTimeReqDTO demonstrationTimeReqDTO); // 해당 상품이 예약 상태인지 확인 가능(실증 장비 신청 페이지에서 대여가능 / 예약 마감 표기 할거임)
		void rentalDateChange(DemonstrationResRentalDTO demonstrationResRentalDTO); // 물품 대여 조회 페이지 연기 신청 및 반납 조기 신청
		void demonstrationReservation(DemonstrationReservationDTO demonstrationReservationDTO); // 실증 신청 상세 페이지에서 예약 신청하기 클릭시, 예약 정보 저장
		void demonstrationReservationCancel(DemonstrationReservationCancelDTO demonstrationReservationCancelDTO); // 실증 신청 상세 페이지에서 예약 취소하기 클릭 시, 예약 정보 취소
		void demonstrationReservationChange(DemonstrationReservationDTO demonstrationReservationDTO); // 실증 신청 상세 페이지에서 예약 변경하기 클릭 시, 예약 정보 수정
		void addDemonstration(DemonstrationFormDTO demonstrationFormDTO,String memId); // 실증 상품 등록 페이지에서 실증 상품 등록하는 기능
		void updateDemonstration(DemonstrationFormDTO demonstrationFormDTO); // 실증 상품 수정하는 기능
		void deleteDemonstration(Long demNum); // 실증 번호를 받아 실증 상품을 삭제하는 기능
		
}