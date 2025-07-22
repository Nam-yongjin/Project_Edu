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
import com.EduTech.dto.demonstration.DemonstrationFormDTO;
import com.EduTech.dto.demonstration.DemonstrationFormUpdateDTO;
import com.EduTech.dto.demonstration.DemonstrationListDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationResRentalDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationCancelDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeReqDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeResDTO;
import com.EduTech.dto.demonstration.PageResponseDTO;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.service.demonstration.DemonstrationService;
import com.EduTech.util.FileUtil;

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
			@RequestParam("pageCount") int pageCount) {

		PageResponseDTO<DemonstrationListReserveDTO> AllDemRes = demonstrationService.getAllDemRes(search, pageCount);
		return AllDemRes;
	}

	// 기업 실증 신청 조회
	@GetMapping("/demReg")
	public PageResponseDTO<DemonstrationListRegistrationDTO> getAllDemRegPage(
			@RequestParam(value = "search", required = false, defaultValue = "") String search,
			@RequestParam("pageCount") int pageCount) {
		PageResponseDTO<DemonstrationListRegistrationDTO> AllDemReg = demonstrationService.getAllDemReg(search,
				pageCount);
		return AllDemReg;
	}

	// 신청한 물품 대여 조회
	// memId는 나중에 서버 인증 토큰으로 꺼내 처리할것(보안 이슈)
	@GetMapping("/demRental")
	public PageResponseDTO<DemonstrationListDTO> getAllDemRentalPage(@RequestParam(value = "search", required = false, defaultValue = "") String search,
			@RequestParam("pageCount") int pageCount) {
		PageResponseDTO<DemonstrationListDTO> AllDemRental = demonstrationService.getAllDemRental(JWTFilter.getMemId(), search,
				pageCount);
		return AllDemRental;
	}

	// 신증 물품 리스트 목록 조회
	@GetMapping("/demList")
	public PageResponseDTO<DemonstrationListDTO> getAllDemListPage(@RequestParam("pageCount") int pageCount) {
		PageResponseDTO<DemonstrationListDTO> AllDemList = demonstrationService.getAllDemList(pageCount);
		return AllDemList;
	}

	// 실증 장비 신청 상세 페이지
	@GetMapping("/demDetail")
	public DemonstrationListDTO getDemDetailList(@RequestParam("demNum") Long demNum) {
		DemonstrationListDTO DemDetail = getDemDetailList(demNum);
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

	// 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	@PutMapping("/ResState")
	public ResponseEntity<String> DemResStateChange(
			@RequestBody DemonstrationApprovalResDTO demonstrationApprovalResDTO) {
		demonstrationService.approveOrRejectDemRes(demonstrationApprovalResDTO);
		return ResponseEntity.ok("Res 상태 변경 성공");
	}

	// 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	@PutMapping("/RegState")
	public ResponseEntity<String> DemRegStateChange(
			@RequestBody DemonstrationApprovalRegDTO demonstrationApprovalRegDTO) {
		demonstrationService.approveOrRejectDemReg(demonstrationApprovalRegDTO);
		return ResponseEntity.ok("Reg 상태 변경 성공");
	}
	
	// 물품 대여 조회 페이지 연기 신청 및 반납 조기 신청
	@PutMapping("/RentalDate")
	public ResponseEntity<String> DemRentalDateChange(@RequestBody DemonstrationResRentalDTO demonstrationResRentalDTO)
	{
		demonstrationService.rentalDateChange(demonstrationResRentalDTO);
		return ResponseEntity.ok("Rental 날짜 변경 성공");
	}
	
	// 실증 신청 상세 페이지에서 예약 신청하기 클릭시, 예약 정보 저장
	@PostMapping("/ReservationRes")
	public ResponseEntity<String> DemResReservation(@RequestBody DemonstrationReservationDTO demonstrationReservationDTO)
	{
		demonstrationService.demonstrationReservation(demonstrationReservationDTO);
		return ResponseEntity.ok("예약 성공");
	}
	
	// 실증 신청 상세 페이지에서 예약 취소하기 클릭 시, 예약 정보 취소
	@DeleteMapping("/CancelRes")
	public ResponseEntity<String> DemResCancel(@RequestBody DemonstrationReservationCancelDTO demonstrationReservationCancelDTO)
	{
		demonstrationService.demonstrationReservationCancel(demonstrationReservationCancelDTO);
		return ResponseEntity.ok("예약 취소 완료");
	}

	// 실증 상품 등록 페이지에서 실증 상품 등록하는 기능
	@PostMapping("/addDem")
	public ResponseEntity<String> DemAdd(@ModelAttribute DemonstrationFormDTO demonstrationFormDTO)
	{

		demonstrationService.addDemonstration(demonstrationFormDTO);
		return ResponseEntity.ok("실증 물품 등록 완료");
	}
	
	// 실증 상품 수정하는 기능
	@PutMapping("/UpdateDem")
	public ResponseEntity<String> DemUpdate(@ModelAttribute DemonstrationFormUpdateDTO demonstrationFormUpdateDTO)
	{
		demonstrationService.updateDemonstration(demonstrationFormUpdateDTO); 
		return ResponseEntity.ok("실증 물품 수정 완료");
	}
	
	// 실증 번호를 받아 실증 상품을 삭제하는 기능
	@DeleteMapping("/DeleteDem")
	public ResponseEntity<String> demDelete(@RequestParam("demNum") Long demNum)
	{
		demonstrationService.deleteDemonstration(demNum);
		return ResponseEntity.ok("실증 물품 삭제 완료");
		// 삭제 시 실증 물품의 기본키를 외래키로 가지고 잇던 튜플 삭제
	}

	
}
