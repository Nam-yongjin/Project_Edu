package com.EduTech.controller.demonstration;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
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
import com.EduTech.dto.demonstration.PageResponseDTO;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.service.demonstration.DemonstrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/demonstration")
public class DemonstrationController {

	private final DemonstrationService demonstrationService;

	// 교사 실증 신청 조회
	@GetMapping("/demRes")
	public PageResponseDTO<DemonstrationListReserveDTO> getAllDemResPage(
			@RequestParam(value = "search", required = false, defaultValue = "") String search,
			@RequestParam("pageCount") Integer pageCount) {
		
		PageResponseDTO<DemonstrationListReserveDTO> AllDemRes = demonstrationService.getAllDemRes(search, pageCount);
		return AllDemRes;
	}

	// 기업 실증 신청 조회
	@GetMapping("/demReg")
	public PageResponseDTO<DemonstrationListRegistrationDTO> getAllDemRegPage(
			@RequestParam(value = "search", required = false, defaultValue = "") String search,
			@RequestParam("pageCount") Integer pageCount) {
		PageResponseDTO<DemonstrationListRegistrationDTO> AllDemReg = demonstrationService.getAllDemReg(search,
				pageCount);
		return AllDemReg;
	}

	// 신청한 물품 대여 조회
	// memId는 나중에 서버 인증 토큰으로 꺼내 처리할것(보안 이슈)
	@GetMapping("/demRental")
	public PageResponseDTO<DemonstrationRentalListDTO> getAllDemRentalPage(
			@RequestParam(value = "search", required = false, defaultValue = "") String search,
			@RequestParam("pageCount") Integer pageCount, @RequestParam("memId") String memId) {
		PageResponseDTO<DemonstrationRentalListDTO> AllDemRental = demonstrationService
				.getAllDemRental(memId, search, pageCount);
		return AllDemRental;
	}

	// 실증 물품 리스트 목록 조회
	@GetMapping("/demList")
	public PageResponseDTO<DemonstrationPageListDTO> getAllDemListPage(@RequestParam("pageCount") Integer pageCount) {
		PageResponseDTO<DemonstrationPageListDTO> AllDemList = demonstrationService.getAllDemList(pageCount);
		return AllDemList;
	}

	// 실증 장비 신청 상세 페이지
	@GetMapping("/demDetail")
	public DemonstrationDetailDTO getDemDetailList(@RequestParam("demNum") Long demNum) {
		DemonstrationDetailDTO DemDetail = getDemDetailList(demNum);
		return DemDetail;
	}

	// 예약 날짜 확인
	@GetMapping("/demResCon")
	public List<DemonstrationTimeResDTO> checkReservationStateList(
			@RequestBody DemonstrationTimeReqDTO demonstrationTimeReqDTO) {
		List<DemonstrationTimeResDTO> checkResList = demonstrationService
				.checkReservationState(demonstrationTimeReqDTO);
		return checkResList;
	}


	// 물품 대여 조회 페이지 연기 신청 및 반납 조기 신청
	@PutMapping("/RentalDate")
	public ResponseEntity<String> DemRentalDateChange(
			@RequestBody DemonstrationResRentalDTO demonstrationResRentalDTO) {
		demonstrationService.rentalDateChange(demonstrationResRentalDTO);
		return ResponseEntity.ok("Rental 날짜 변경 성공");
	}

	// 실증 신청 상세 페이지에서 예약 신청하기 클릭시, 예약 정보 저장
	@PostMapping("/ReservationRes")
	public ResponseEntity<String> DemResReservation(
			@RequestBody DemonstrationReservationDTO demonstrationReservationDTO) {
		demonstrationService.demonstrationReservation(demonstrationReservationDTO);
		return ResponseEntity.ok("예약 성공");
	}

	// 실증 신청 상세 페이지에서 예약 취소하기 클릭 시, 예약 정보 취소
	@DeleteMapping("/CancelRes")
	public ResponseEntity<String> DemResCancel(
			@RequestBody DemonstrationReservationCancelDTO demonstrationReservationCancelDTO) {
		demonstrationService.demonstrationReservationCancel(demonstrationReservationCancelDTO);
		return ResponseEntity.ok("예약 취소 완료");
	}
	
	// 실증 신청 상세 페이지에서 예약 변경하기 클릭 시, 예약 정보 변경
	@PutMapping("/ChangeRes")
	public ResponseEntity<String> DemResChange(@RequestBody DemonstrationReservationDTO demonstrationReservationDTO)
	{
		demonstrationService.demonstrationReservation(demonstrationReservationDTO);
		return ResponseEntity.ok("예약 변경 성공");
	}

	// 실증 상품 등록 페이지에서 실증 상품 등록하는 기능
	@PostMapping("/addDem")
	public ResponseEntity<String> DemAdd(@ModelAttribute DemonstrationFormDTO demonstrationFormDTO) {

		demonstrationService.addDemonstration(demonstrationFormDTO);
		return ResponseEntity.ok("실증 물품 등록 완료");
	}

	// 실증 상품 수정하는 기능
	@PutMapping("/UpdateDem")
	public ResponseEntity<String> DemUpdate(@ModelAttribute DemonstrationFormDTO demonstrationFormDTO) {
		demonstrationService.updateDemonstration(demonstrationFormDTO);
		return ResponseEntity.ok("실증 물품 수정 완료");
	}

	// 실증 번호를 받아 실증 상품을 삭제하는 기능
	@DeleteMapping("/DeleteDem")
	public ResponseEntity<String> demDelete(@RequestParam("demNum") Long demNum) {
		demonstrationService.deleteDemonstration(demNum);
		return ResponseEntity.ok("실증 물품 삭제 완료");
		// 삭제 시 실증 물품의 기본키를 외래키로 가지고 잇던 튜플 삭제
	}
	
	
	
	

}
