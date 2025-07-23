package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

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
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.event.EventUse;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.demonstration.DemonstrationService;
import com.EduTech.service.event.EventServiceImpl;
import com.EduTech.util.FileUtil;

@SpringBootTest
public class DemonstrationServiceTests {
	@Autowired
	DemonstrationReserveRepository demonstrationReserveRepository;
	@Autowired
	DemonstrationRegistrationRepository demonstrationRegistrationRepository;
	@Autowired
	DemonstrationRepository demonstrationRepository;
	@Autowired
	DemonstrationTimeRepository demonstrationTimeRepository;
	@Autowired
	DemonstrationImageRepository demonstrationImageRepository;
	@Autowired
	FileUtil fileUtil;
	@Autowired
	private DemonstrationService demonstrationService;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@MockBean
	EventServiceImpl eventServiceImpl;
	@MockBean
	EventState eventState;
	@MockBean
	EventUse eventUse;

	// @Test
	@DisplayName("검색어 없는 실증 교사 조회 테스트")
	void getAllDemResWithoutSearch() {
		// given
		String search = "";
		int pageCount = 0;

		// when
		PageResponseDTO<DemonstrationListReserveDTO> response = demonstrationService.getAllDemRes(search, pageCount);

		// then
		assertNotNull(response);
		System.out.println("총 페이지 수: " + response.getTotalPages());
		System.out.println("현재 페이지: " + response.getCurrentPage());
		System.out.println("데이터 목록:");

		response.getContent().forEach(item -> {
			System.out.println(item);
		});
	}

	// @Test
	@DisplayName("검색어로 필터링된 실증 교사 페이지 조회 테스트")
	void getAllDemResWithSearch() {
		// given
		String search = "자동";
		int pageCount = 0;

		// when
		PageResponseDTO<DemonstrationListReserveDTO> response = demonstrationService.getAllDemRes(search, pageCount);

		// then
		assertNotNull(response);
		System.out.println("총 페이지 수: " + response.getTotalPages());
		System.out.println("현재 페이지: " + response.getCurrentPage());
		System.out.println("데이터 목록:");

		response.getContent().forEach(item -> {
			System.out.println(item);
		});
	}

	// @Test
	@DisplayName("검색어 없는 실증 기업 조회 테스트")
	void getAllDemRegWithoutSearch() {
		// given
		String search = "";
		int pageCount = 0;

		// when
		PageResponseDTO<DemonstrationListRegistrationDTO> response = demonstrationService.getAllDemReg(search,
				pageCount);

		// then
		assertNotNull(response); // null 일경우 테스트 실패
		System.out.println("총 페이지 수: " + response.getTotalPages());
		System.out.println("현재 페이지: " + response.getCurrentPage());
		System.out.println("데이터 목록:");

		response.getContent().forEach(item -> {
			System.out.println(item);
		});
	}

	// @Test
	@DisplayName("검색어로 필터링된 실증 기업 페이지 조회 테스트")
	void getAllDemRegWithSearch() {
		// given
		String search = "털";
		int pageCount = 0;

		// when
		PageResponseDTO<DemonstrationListRegistrationDTO> response = demonstrationService.getAllDemReg(search,
				pageCount);

		// then
		assertNotNull(response);
		System.out.println("총 페이지 수: " + response.getTotalPages());
		System.out.println("현재 페이지: " + response.getCurrentPage());
		System.out.println("데이터 목록:");

		response.getContent().forEach(item -> {
			System.out.println(item);
		});
	}
/* Admin으로 뺏음
	// @Test
	@DisplayName("실증 교사 신청 조회 승인 / 거부 상태 변화 테스트")
	void changeResState() {
		DemonstrationApprovalResDTO demonstrationApprovalResDTO = new DemonstrationApprovalResDTO();
		demonstrationApprovalResDTO.setDemonstrationState(DemonstrationState.REJECT);
		demonstrationApprovalResDTO.setDemRevNum(Long.valueOf(161));
		demonstrationApprovalResDTO.setMemId(Member.builder().memId("user9").build().getMemId());
		demonstrationService.approveOrRejectDemRes(demonstrationApprovalResDTO);
	}

	// @Test
	@DisplayName("실증 기업 신청 조회 승인 / 거부 상태 변화 테스트")
	void changeRegState() {
		DemonstrationApprovalRegDTO demonstrationApprovalRegDTO = new DemonstrationApprovalRegDTO();
		demonstrationApprovalRegDTO.setDemonstrationState(DemonstrationState.WAIT);
		demonstrationApprovalRegDTO.setDemRegNum(Long.valueOf(10));
		demonstrationApprovalRegDTO.setMemId(Member.builder().memId("user").build().getMemId());
		demonstrationService.approveOrRejectDemReg(demonstrationApprovalRegDTO);
	}
*/
	// @Test
	@DisplayName("실증 신청 물품 대여 현황 조회 테스트")
	void getDemRental() {
		String search = "product";
		int pageCount = 0;
		String memId = "user2";
		PageResponseDTO<DemonstrationRentalListDTO> response = demonstrationService.getAllDemRental(memId, search,
				pageCount);

		// then
		assertNotNull(response);
		System.out.println("총 페이지 수: " + response.getTotalPages());
		System.out.println("현재 페이지: " + response.getCurrentPage());
		System.out.println("데이터 목록:");

		response.getContent().forEach(item -> {
			System.out.println(item);
		});

	}

	// @Test
	@DisplayName("물품 대여 조회 페이지 연기 신청 및 반납 조기 신청 테스트")
	void getDemRentalChangeDate() {
		DemonstrationResRentalDTO demonstrationResRentalDTO = new DemonstrationResRentalDTO();
		demonstrationResRentalDTO.setDemNum(Long.valueOf(10));
		demonstrationResRentalDTO.setDemRevNum(Long.valueOf(155));
		demonstrationResRentalDTO.setMemId("user3");
		demonstrationResRentalDTO.setOriginStartDate(LocalDate.parse("2025-07-15"));
		demonstrationResRentalDTO.setOriginEndDate(LocalDate.parse("2025-07-17"));
		demonstrationResRentalDTO.setUpdatedStartDate(LocalDate.parse("2025-07-10"));
		demonstrationResRentalDTO.setUpdatedEndDate(LocalDate.parse("2025-08-10"));
		demonstrationService.rentalDateChange(demonstrationResRentalDTO);
	}

	//@Test
	@DisplayName("실증 물품 리스트 목록 조회")
	void getDemList() {
		PageResponseDTO<DemonstrationPageListDTO> response = demonstrationService.getAllDemList(0);
		// then
		assertNotNull(response);
		System.out.println("총 페이지 수: " + response.getTotalPages());
		System.out.println("현재 페이지: " + response.getCurrentPage());
		System.out.println("데이터 목록:");

		response.getContent().forEach(item -> {
			System.out.println(item);
		});

	}
	
	
	
	//@Test
	@DisplayName("예약 상태 확인")
	void checkReservation() {
		List<DemonstrationTimeResDTO> resCheck=new ArrayList<>();
		DemonstrationTimeReqDTO demonstrationTimeReqDTO=new DemonstrationTimeReqDTO();
		demonstrationTimeReqDTO.setDemNum(Long.valueOf(105));
		demonstrationTimeReqDTO.setStartDate(LocalDate.parse("2025-08-01"));
		demonstrationTimeReqDTO.setEndDate(LocalDate.parse("2025-08-31"));
		resCheck=demonstrationService.checkReservationState(demonstrationTimeReqDTO);
		for(DemonstrationTimeResDTO dto: resCheck)
		{
			System.out.println(dto);
		}
		
	}
	
	
	//@Test
	@DisplayName("실증 장비 신청 상세 페에지")
	void demDetail()
	{
		Long demNum=Long.valueOf(111);
		DemonstrationDetailDTO demonstrationDetailDTO=demonstrationService.getDemDetailList(demNum);
		System.out.println(demonstrationDetailDTO);
	}
	
	@Test
	@DisplayName("예약 테스트")
	void reservationDem() {
		DemonstrationReservationDTO demonstrationReservationDTO=new DemonstrationReservationDTO();
		demonstrationReservationDTO.setMemId("user2");
		demonstrationReservationDTO.setDemNum(Long.valueOf(105));
		demonstrationReservationDTO.setApplyAt(LocalDate.now());
		demonstrationReservationDTO.setDemonstrationstate(DemonstrationState.ACCEPT);
		demonstrationReservationDTO.setStartDate(LocalDate.parse("2025-08-20"));
		demonstrationReservationDTO.setEndDate(LocalDate.parse("2025-08-25"));
		
		demonstrationService.demonstrationReservation(demonstrationReservationDTO);
	}


	
	//@Test
	@DisplayName("예약 취소")
	void reservationCancel() {
		DemonstrationReservationCancelDTO demonstrationReservationCancelDTO=new DemonstrationReservationCancelDTO();
		demonstrationReservationCancelDTO.setDemNum(Long.valueOf(105));
		demonstrationReservationCancelDTO.setMemId("user2");
		demonstrationService.demonstrationReservationCancel(demonstrationReservationCancelDTO);
	}
	
	
	
	//@Test
	@DisplayName("실증 물품 등록")
	void addDem() {

		DemonstrationFormDTO demonstrationFormDTO = new DemonstrationFormDTO();
		demonstrationFormDTO.setDemInfo("이 상품은 아주 멋진 상품입니다.");
		demonstrationFormDTO.setDemMfr("아주 멋진 상품을 만든 제조사 입니다.");
		demonstrationFormDTO.setDemName("멋진 상품임");
		demonstrationFormDTO.setExpDate(LocalDate.parse("2025-10-20"));
		demonstrationFormDTO.setItemNum(Long.valueOf(120));
		demonstrationFormDTO.setMemId("user3");

		try {
			List<MultipartFile> imageList = new ArrayList<>();
			Path imagePath = Path.of("C:\\duke.png");
			Path imagePath2 = Path.of("C:\\셔츠1.jpg");
			byte[] imageBytes = Files.readAllBytes(imagePath);
			byte[] imageBytes2 = Files.readAllBytes(imagePath2);
			MockMultipartFile mockFile = new MockMultipartFile("file", // form field name
					"duke.png", // 원래 파일명
					"image/png", // content type
					imageBytes // 실제 파일 내용 (byte 배열)
			);
			MockMultipartFile mockFile2 = new MockMultipartFile("file", // form field name
					"바지1.jpg", // 원래 파일명
					"image/jpg", // content type
					imageBytes2 // 실제 파일 내용 (byte 배열)
			);
			imageList.add(mockFile);
			imageList.add(mockFile2);
			demonstrationFormDTO.setImageList(imageList);
		} catch (Exception e) {

		}
		demonstrationService.addDemonstration(demonstrationFormDTO);
	}
	
	
	//@Test
	@DisplayName("실증 상품 등록 수정")
	public void DemDelete()
	{
		DemonstrationFormDTO demonstrationFormDTO=new DemonstrationFormDTO();
		demonstrationFormDTO.setDemInfo("이 상품은 멋진 상품이 아닙니다.");
		demonstrationFormDTO.setDemMfr("아주 못생긴 상품을 만든 제조사 입니다.");
		demonstrationFormDTO.setDemName("멋지지 않은 상품임");
		demonstrationFormDTO.setExpDate(LocalDate.parse("2025-12-25"));
		demonstrationFormDTO.setItemNum(Long.valueOf(120));
		demonstrationFormDTO.setMemId("user3");
		demonstrationFormDTO.setDemNum(Long.valueOf(111));
		
		
		try { 
			List<MultipartFile> imageList = new ArrayList<>();
			Path imagePath = Path.of("C:\\바지이미지.jpg");
			//Path imagePath2 = Path.of("C:\\셔츠3.jpg");
			Path imagePath3 = Path.of("C:\\duke.png");
			byte[] imageBytes = Files.readAllBytes(imagePath);
			//byte[] imageBytes2 = Files.readAllBytes(imagePath2);
			byte[] imageBytes3 = Files.readAllBytes(imagePath3);
			MockMultipartFile mockFile = new MockMultipartFile("file", // form field name
					"바지이미지.jpg", // 원래 파일명
					"image/jpg", // content type
					imageBytes // 실제 파일 내용 (byte 배열)
			);
			/*MockMultipartFile mockFile2 = new MockMultipartFile("file", // form field name
					"바지이미지.jpg", // 원래 파일명
					"image/jpg", // content type
					imageBytes2 // 실제 파일 내용 (byte 배열)
			); */
			MockMultipartFile mockFile3 = new MockMultipartFile("file", // form field name
					"duke.png", // 원래 파일명
					"image/png", // content type
					imageBytes3 // 실제 파일 내용 (byte 배열)
			);
			imageList.add(mockFile);
		//	imageList.add(mockFile2);
			imageList.add(mockFile3);
			
			demonstrationFormDTO.setImageList(imageList);
			
		} catch (Exception e) {

		}
	
		demonstrationService.updateDemonstration(demonstrationFormDTO); 
	}
	
	//@Test
	@DisplayName("상품 삭제 테스트")
	public void deleteDem() {
		Long demNum=Long.valueOf(111);
		
		demonstrationService.deleteDemonstration(demNum);
	}
}
