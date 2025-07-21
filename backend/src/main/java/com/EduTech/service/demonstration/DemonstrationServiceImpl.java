package com.EduTech.service.demonstration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.dto.demonstration.DemonstrationFormDTO;
import com.EduTech.dto.demonstration.DemonstrationListDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationResRentalDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationCancelDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeDTO;
import com.EduTech.dto.demonstration.PageResponseDTO;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DemonstrationServiceImpl implements DemonstrationService {
	@Autowired
	DemonstrationReserveRepository demonstrationReserveRepository;
	@Autowired
	DemonstrationRegistrationRepository demonstrationRegistrationRepository;
	@Autowired
	DemonstrationRepository demonstrationRepository;
	
	
	// 실증 교사 신청목록 조회 기능 (검색도 같이 구현할 것임.)
	@Override
	public List<DemonstrationListReserveDTO> findAllDemRes(String search) {

		// getAllPagedResults 의 매개변수로 익명클래스, 정렬한 칼럼 이름, 페이지 전달
		// Function<Pageable>은, apply에 필요한 매개값
		// function<Page<DemonstrationListReserveDTO>는 반환값 이라생각
		if (search.equals("")) { // 검색어 입력이 없을 경우,
			List<DemonstrationListReserveDTO> allResults = getAllPagedResults(
					new Function<Pageable, Page<DemonstrationListReserveDTO>>() {
						@Override
						public Page<DemonstrationListReserveDTO> apply(Pageable pageable) {
							return demonstrationReserveRepository.selectPageDemRes(pageable);
						}
					}, "demRevNum", 5);
			return allResults;
		}

		else { // 검색어를 입력 했을 경우,
			List<DemonstrationListReserveDTO> allResults = getAllPagedResults(
					new Function<Pageable, Page<DemonstrationListReserveDTO>>() {
						@Override
						public Page<DemonstrationListReserveDTO> apply(Pageable pageable) {
							return demonstrationReserveRepository.selectPageDemResSearch(pageable, search);
						}
					}, "demRevNum", 5);
			return allResults;
		}
	}

	// 실증 기업 신청목록 조회 기능 (검색도 같이 구현할 것임.)
	@Override
	public List<DemonstrationListRegistrationDTO> findAllDemReg(String search) {
		
		if (search.equals("")) { // 검색어 입력이 없을 경우,
			List<DemonstrationListRegistrationDTO> allResults = getAllPagedResults(
					new Function<Pageable, Page<DemonstrationListRegistrationDTO>>() {
						@Override
						public Page<DemonstrationListRegistrationDTO> apply(Pageable pageable) {
							return demonstrationRegistrationRepository.selectPageDemReg(pageable);
						}
					}, "demRegNum", 5);
			return allResults;
		}

		else { // 검색어를 입력 했을 경우,
			List<DemonstrationListRegistrationDTO> allResults = getAllPagedResults(
					new Function<Pageable, Page<DemonstrationListRegistrationDTO>>() {
						@Override
						public Page<DemonstrationListRegistrationDTO> apply(Pageable pageable) {
							return demonstrationRegistrationRepository.selectPageDemRegSearch(pageable, search);
						}
					}, "demRegNum", 5);
			return allResults;
		}
	}

	// 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	@Override
	public void approveOrRejectDemRes(DemonstrationApprovalResDTO demonstrationApprovalResDTO) {
		demonstrationReserveRepository.updateDemResChangeState(demonstrationApprovalResDTO.getDemonstrationState(),demonstrationApprovalResDTO.getMemId(),demonstrationApprovalResDTO.getDemRevNum());
	}

	// 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	@Override
	public void approveOrRejectDemReg(DemonstrationApprovalRegDTO demonstrationApprovalRegDTO) {
		demonstrationRegistrationRepository.updateDemResChangeState(demonstrationApprovalRegDTO.getDemonstrationState(),demonstrationApprovalRegDTO.getMemId(),demonstrationApprovalRegDTO.getDemRegNum());
	}

	// 회원이 신청한 물품 대여 조회 페이지 조회 기능 (검색도 같이 구현할 것임.)
	@Override
	public PageResponseDTO<DemonstrationListDTO> findAllDemRental(String memId, int pageCount,String search) {
		
		if(search.equals("")) { // 검색어 입력이 없을 경우,
		Page<DemonstrationListDTO> currentPage = demonstrationRepository
				.selectPageViewDem(PageRequest.of(pageCount, 10, Sort.by("demNum").descending()),memId);

		return new PageResponseDTO <DemonstrationListDTO>(currentPage); // 페이지 DTO 객체 리턴
		}
		else { // 검색어 입력을 했을 경우,
			Page<DemonstrationListDTO> currentPage = demonstrationRepository
					.selectPageViewDemSearch(PageRequest.of(pageCount, 10, Sort.by("demNum").descending()),memId,search);

			return new PageResponseDTO <DemonstrationListDTO>(currentPage); // 페이지 DTO 객체 리턴
		}
		
	}
/*
	// 물품 대여 조회 페이지 연기 신청 및 반납 조기 신청
	public void rentalDateChange(DemonstrationResRentalDTO demonstrationResRentalDTO) {
		
	}

	// 실증 장비신청 페이지 (실증 물품 리스트 목록)
	public List<DemonstrationListDTO> findAllDemList() {

	}

	// 해당 상품이 예약 상태인지 확인 가능 (실증 장비 신청 페이지에서 대여가능 / 예약 마감 표기 할거임)
	public List<DemonstrationTimeDTO> checkReservationState(Long demNum) {

	}

	// 실증 장비 신청 상세 페이지
	public List<DemonstrationListDTO> findDemDetailList(Long demNum) {

	}

	// 실증 신청 상세 페이지에서 예약 신청하기 클릭시, 예약 정보 저장
	public void demonstrationReservation(DemonstrationReservationDTO demonstrationReservationDTO) {

	}

	// 실증 신청 상세 페이지에서 예약 취소하기 클릭 시, 예약 정보 취소
	public void demonstrationReservationCancel(DemonstrationReservationCancelDTO demonstrationReservationCancelDTO) {

	}

	// 실증 상품 등록 페이지에서 실증 상품 등록하는 기능
	public void addDemonstration(DemonstrationFormDTO demonstrationFormDTO) {

	}
*/
	// 함수형 인터페이스 Function을 사용해서 리포지토리 호출을 메소드 바깥에서 함수로 만들어 넘길수 있음.
	// Function<입력값, 출력값> apply의 매개변수: 입력값, 반환값: 출력값
	// 이런느낌
	public <T> List<T> getAllPagedResults(Function<Pageable, Page<T>> pageFetcher, String sortColumn, int pageSize) {
		List<Page<T>> allData = new ArrayList<>();
		Page<T> firstPage = pageFetcher.apply(PageRequest.of(0, pageSize, Sort.by(sortColumn).descending()));
		int totalPageCount = firstPage.getTotalPages();

		for (int i = 0; i < totalPageCount; i++) {
			Page<T> page = pageFetcher.apply(PageRequest.of(i, pageSize, Sort.by(sortColumn).descending()));
			allData.add(page);
		}

		List<T> result = new ArrayList<>();
		for (Page<T> page : allData) {
			result.addAll(page.getContent());
		}
		return result;
	}

}
