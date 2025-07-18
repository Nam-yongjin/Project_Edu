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
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
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
	
	
	// 실증 교사 신청목록 조회 기능 (검색도 같이 구현할 것임.)
	public List<DemonstrationListReserveDTO> findAllDemRes(String search) {
	
	if(search.equals("")) { // 검색어 입력이 없을 경우,
		List<DemonstrationListReserveDTO> allResults = getAllPagedResults(
			    new Function<Pageable, Page<DemonstrationListReserveDTO>>() {
			        @Override
			        public Page<DemonstrationListReserveDTO> apply(Pageable pageable) {
			            return demonstrationReserveRepository.selectPageDemRes(pageable);
			        }
			    },
			    "demRevNum",
			    5
			);
	}
	
	else { // 검색어를 입력 했을 경우,
		pageFetcher = new Function<Pageable, Page<DemonstrationListReserveDTO>>() {
	        @Override
	        public Page<DemonstrationListReserveDTO> apply(Pageable pageable) {
	            return demonstrationReserveRepository.selectPageDemResSearch(pageable, search);
	        }
	    };
	}
	}

	
	// 실증 기업 신청목록 조회 기능 (검색도 같이 구현할 것임.)
	public List<DemonstrationListRegistrationDTO> findAllDemReg(String searchText) {
		List<Page<DemonstrationListRegistrationDTO>> allData = new ArrayList<>();
		Page<DemonstrationListRegistrationDTO> firstPage = demonstrationRegistrationRepository
				.selectPageDemReg(PageRequest.of(0, 5, Sort.by("demRegNum").descending()));
		int totalPageCount = firstPage.getTotalPages();

		for (int i = 0; i < totalPageCount; i++) {
			Page<DemonstrationListRegistrationDTO> totalPage = demonstrationRegistrationRepository
					.selectPageDemReg(PageRequest.of(i, 5, Sort.by("demRegNum").descending()));
			allData.add(totalPage);
		}

		List<DemonstrationListRegistrationDTO> AllReserve=new ArrayList<DemonstrationListRegistrationDTO>();
		for (int i = 0; i < totalPageCount; i++) {
			AllReserve.addAll(allData.get(i).getContent()); // 페이지 목록 출력 (페이지 접근시 get(i))
		}
		return AllReserve;
	}

	// 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	public void approveOrRejectDemRes(DemonstrationResApporvalDTO demonstrationResApporvalDTO) {
		
	}

	// 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	public void approveOrRejectDemReg(DemonstrationRegApporvalDTO demonstrationRegApporvalDTO) {
		
	}

	// 회원이 신청한 물품 대여 조회 페이지 조회 기능 (검색도 같이 구현할 것임.)
	public List<DemonstrationListDTO> findAllDemRental(String memId) {
		
	}

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
	
	
	
	// 함수형 인터페이스 Function을 사용해서 리포지토리 호출을 메소드 바깥에서 함수로 만들어 넘길수 있음.
	// Function<입력값, 출력값>
	public <T> List<T> getAllPagedResults(Function<Pageable, Page<T>> pageFetcher, String sortColumn, int pageSize) 
	{
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
