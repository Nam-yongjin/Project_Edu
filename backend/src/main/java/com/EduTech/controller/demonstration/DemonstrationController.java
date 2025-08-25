package com.EduTech.controller.demonstration;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.demonstration.DemonstrationBorrowListDTO;
import com.EduTech.dto.demonstration.DemonstrationDetailDTO;
import com.EduTech.dto.demonstration.DemonstrationFormReqDTO;
import com.EduTech.dto.demonstration.DemonstrationFormResDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationPageListDTO;
import com.EduTech.dto.demonstration.DemonstrationRentalListDTO;
import com.EduTech.dto.demonstration.DemonstrationResRentalDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationDTO;
import com.EduTech.dto.demonstration.DemonstrationSearchDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeReqDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeResDTO;
import com.EduTech.dto.demonstration.ResRequestDTO;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.service.demonstration.DemonstrationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/demonstration")
public class DemonstrationController {

	private final DemonstrationService demonstrationService;

	// 기업들이 등록한 물품들에 대여 중인 선생들의 정보 조회
	@PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
	@GetMapping("/borrowRes")
	public PageResponseDTO<DemonstrationListReserveDTO> getAllDemResBorrowPage(
			@ModelAttribute DemonstrationSearchDTO demonstrationSearchDTO) {
		String memId = JWTFilter.getMemId();
		PageResponseDTO<DemonstrationListReserveDTO> AllDemRes = demonstrationService.getAllDemResRental(demonstrationSearchDTO,memId);
		return AllDemRes;
	}
	
	// 신청한 물품 대여 조회
	@PreAuthorize("hasRole('TEACHER')")
	@GetMapping("/demRental")
	public PageResponseDTO<DemonstrationRentalListDTO> getAllDemRentalPage(
			@ModelAttribute DemonstrationSearchDTO demonstrationSearchDTO) {
		String memId = JWTFilter.getMemId();
		PageResponseDTO<DemonstrationRentalListDTO> AllDemRental = demonstrationService.getAllDemRental(memId,
				demonstrationSearchDTO);
		return AllDemRental;
	}

	// 실증 물품 리스트 목록 조회
	@GetMapping("/demList")
	public PageResponseDTO<DemonstrationPageListDTO> getAllDemListPage(@RequestParam("pageCount") Integer pageCount,
			@RequestParam(value = "type", defaultValue = "demName") String type,
			@RequestParam(value = "search", defaultValue = "") String search,
			@RequestParam(value="sortType",defaultValue="asc")String sortType) {
		PageResponseDTO<DemonstrationPageListDTO> AllDemList = demonstrationService.getAllDemList(pageCount, type,
				search,sortType);

		return AllDemList;
	}

	// 실증 장비 신청 상세 페이지
	@GetMapping("/demDetail")
	public DemonstrationDetailDTO getDemDetailList(@RequestParam("demNum") Long demNum) {
		DemonstrationDetailDTO DemDetail = demonstrationService.getDemDetailList(demNum);
		return DemDetail;
	}

	// 예약 날짜 확인
	@GetMapping("/demResCon")
	public List<DemonstrationTimeResDTO> checkReservationStateList(DemonstrationTimeReqDTO demonstrationTimeReqDTO) {
		List<DemonstrationTimeResDTO> checkResList = demonstrationService
				.checkReservationState(demonstrationTimeReqDTO);
		return checkResList;
	}

	// 현재 회원을 제외한 예약 날짜 확인
	@GetMapping("/demResConExcept")
	public List<DemonstrationTimeResDTO> checkReservationStateExcept(DemonstrationTimeReqDTO demonstrationTimeReqDTO) {
		String memId = JWTFilter.getMemId();
		List<DemonstrationTimeResDTO> checkResList = demonstrationService
				.checkReservationStateExcept(demonstrationTimeReqDTO, memId);
		return checkResList;
	}

	// 관리자가 신청을 받아서 반납 신청 및 반납일 연기
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/RentalDate")
	public ResponseEntity<String> DemRentalDateChange(
			@RequestBody DemonstrationResRentalDTO demonstrationResRentalDTO) {
		demonstrationService.rentalDateChange(demonstrationResRentalDTO);
		return ResponseEntity.ok("Rental 날짜 변경 성공");
	}

	// 실증 신청 상세 페이지에서 예약 신청하기 클릭시, 예약 정보 저장
	@PreAuthorize("hasRole('TEACHER')")
	@PostMapping("/ReservationRes")
	public ResponseEntity<String> DemResReservation(
			@RequestBody DemonstrationReservationDTO demonstrationReservationDTO) {
		String memId = JWTFilter.getMemId();
		demonstrationService.demonstrationReservation(demonstrationReservationDTO, memId);
		return ResponseEntity.ok("예약 성공");
	}

	// 예약 취소
	@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
	@DeleteMapping("/CancelRes")
	public ResponseEntity<String> DemResCancel(@RequestBody List<Long> demRevNum) {
	    demonstrationService.demonstrationReservationCancel(demRevNum);
	    return ResponseEntity.ok("예약 취소 완료");
	}


	// 물품 대여 목록 페이지에서 예약 변경하기 클릭 시, 예약 정보 변경
	@PreAuthorize("hasRole('TEACHER')")
	@PutMapping("/ChangeRes")
	public ResponseEntity<String> DemResChange(@RequestBody DemonstrationReservationDTO demonstrationReservationDTO) {
		System.out.println(demonstrationReservationDTO);
		String memId = JWTFilter.getMemId();
		demonstrationService.demonstrationReservationChange(demonstrationReservationDTO, memId);
		return ResponseEntity.ok("예약 변경 성공");
	}

	// 물품 대여 목록 페이지에서 대여 연장 / 반납 버튼 클릭 시 해당 정보가 db에 저장
	@PreAuthorize("hasRole('TEACHER')")
	@PostMapping("/AddRequest")
	public ResponseEntity<String> addRequest(@RequestBody ResRequestDTO resRequestDTO) {
		System.out.println(resRequestDTO);
		String memId = JWTFilter.getMemId();
		demonstrationService.addRequest(resRequestDTO, memId);
		return ResponseEntity.ok("요청 성공");
	}
	@PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
	@PostMapping("/addDem") // 이미지 파일 업로드 관련해서 8개까지 밖에 컨트롤러에 도달 못함(아마 톰켓 서버 관련 설정일듯, 톰켓 서버 설정 바꿔도 안먹음)
	public ResponseEntity<String> DemAdd(
			@RequestPart("demonstrationFormDTO") @Valid DemonstrationFormReqDTO demonstrationFormDTO,
			@RequestPart("imageList") List<MultipartFile> imageList) {
		String memId = JWTFilter.getMemId();
		demonstrationService.addDemonstration(demonstrationFormDTO, imageList, memId);

		return ResponseEntity.ok("실증 물품 등록 완료");
	}

	// 실증 상품 수정하는 기능
	@PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
	@PutMapping("/UpdateDem")
	public ResponseEntity<String> DemUpdate(
			@RequestPart("demonstrationFormDTO") @Valid DemonstrationFormReqDTO demonstrationFormDTO,
			@RequestPart("imageList") List<MultipartFile> imageList) {
		String memId = JWTFilter.getMemId();
		demonstrationService.updateDemonstration(demonstrationFormDTO, imageList, memId);
		return ResponseEntity.ok("실증 물품 수정 완료");
	}

	// 실증 번호를 받아 실증 상품을 삭제하는 기능
	@PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
	@DeleteMapping("/DeleteDem")
	public ResponseEntity<String> demDelete(@RequestBody List<Long> demNum) {
		System.out.println(demNum);
		demonstrationService.deleteDemonstration(demNum);
		return ResponseEntity.ok("실증 물품 삭제 완료");
		// 삭제 시 실증 물품의 기본키를 외래키로 가지고 잇던 튜플 삭제
	}

	// 실증 등록 수정 페이지에서 실증번호를 받아와 실증 상품의 정보를 받아오는 기능
	@PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
	@GetMapping("/SelectOne")
	public DemonstrationFormResDTO SelectOne(@RequestParam("demNum") Long demNum) {
		DemonstrationFormResDTO dto = demonstrationService.selectOne(demNum);
		return dto;
		// 삭제 시 실증 물품의 기본키를 외래키로 가지고 잇던 튜플 삭제
	}

	// 물품 상세정보 페이지에서 현재 회원이 해당 물품에 예약이 되어있을 경우를 나타내는 기능
	@GetMapping("/demReserveCheck")
	public Boolean ReserveCheck(@RequestParam("demNum") Long demNum) {
		String memId = JWTFilter.getMemId();
		Boolean bool = demonstrationService.checkRes(demNum, memId);
		return bool;
		// 삭제 시 실증 물품의 기본키를 외래키로 가지고 잇던 튜플 삭제
	}
	
	// 실증 물품 현황 페이지에서 물품에 대한 정보를 받아오는 기능
	@PreAuthorize("hasAnyRole('ADMIN', 'COMPANY')")
	@GetMapping("/getBorrow") 
	public PageResponseDTO<DemonstrationBorrowListDTO> getBorrow(@ModelAttribute DemonstrationSearchDTO demonstrationSearchDTO) {
		String memId = JWTFilter.getMemId();
		PageResponseDTO<DemonstrationBorrowListDTO> AllgetBorrow = demonstrationService.AllgetBorrow(memId,demonstrationSearchDTO);
		return AllgetBorrow;
	}

}
