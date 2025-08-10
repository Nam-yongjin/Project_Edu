package com.EduTech.service.demonstration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.demonstration.DemonstrationBorrowListDTO;
import com.EduTech.dto.demonstration.DemonstrationDetailDTO;
import com.EduTech.dto.demonstration.DemonstrationFormReqDTO;
import com.EduTech.dto.demonstration.DemonstrationFormResDTO;
import com.EduTech.dto.demonstration.DemonstrationImageDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationPageListDTO;
import com.EduTech.dto.demonstration.DemonstrationRentalListDTO;
import com.EduTech.dto.demonstration.DemonstrationResRentalDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationDTO;
import com.EduTech.dto.demonstration.DemonstrationSearchDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeReqDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeResDTO;
import com.EduTech.dto.demonstration.ResRequestDTO;
import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationImage;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationRequest;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.DemonstrationTime;
import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.Teacher;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRequestRepository;
import com.EduTech.repository.demonstration.DemonstrationReservationSpecs;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveSpecs;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.util.FileUtil;

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
	@Autowired
	DemonstrationTimeRepository demonstrationTimeRepository;
	@Autowired
	DemonstrationImageRepository demonstrationImageRepository;
	@Autowired
	FileUtil fileUtil;
	@Autowired
	DemonstrationRequestRepository demonstrationRequestRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ModelMapper modelMapper; 


	// 실증 교사 신청목록 조회 기능 (검색도 같이 구현할 것임.) -관리자용
	@Override
	public PageResponseDTO<DemonstrationListReserveDTO> getAllDemRes(String search, Integer pageCount) {
		if (pageCount == null || pageCount < 0) {
			pageCount = 0;
		}

		if (!StringUtils.hasText(search)) { // 검색어 입력이 없을 경우,
			Page<DemonstrationListReserveDTO> currentPage = demonstrationReserveRepository
					.selectPageDemRes(PageRequest.of(pageCount, 10, Sort.by("demRevNum").descending()));

			return new PageResponseDTO<DemonstrationListReserveDTO>(currentPage); // 페이지 DTO 객체 리턴
		}

		else { // 검색어를 입력 했을 경우,
			Page<DemonstrationListReserveDTO> currentPage = demonstrationReserveRepository
					.selectPageDemResSearch(PageRequest.of(pageCount, 10, Sort.by("demRevNum").descending()), search);

			return new PageResponseDTO<DemonstrationListReserveDTO>(currentPage); // 페이지 DTO 객체 리턴
		}
	}

	// 서비스 메소드 - DemonstrationReserve 기준으로 페이징 조회 및 DTO 매핑
	public PageResponseDTO<DemonstrationListReserveDTO> getAllDemResRental(DemonstrationSearchDTO searchDTO, String memId) {

	    Integer pageCount = searchDTO.getPageCount();
	    String type = searchDTO.getType();
	    String search = searchDTO.getSearch();
	    String sortBy = searchDTO.getSortBy();
	    String sort = searchDTO.getSort();
	    String statusFilter = searchDTO.getStatusFilter();
	    Long demNum = searchDTO.getDemNum();

	    if (pageCount == null || pageCount < 0)
	        pageCount = 0;
	    if (!StringUtils.hasText(sortBy))
	        sortBy = "applyAt";
	    if (!StringUtils.hasText(sort))
	        sort = "desc";

	    Pageable pageable = PageRequest.of(pageCount, 10);

	    Specification<DemonstrationReserve> spec = DemonstrationReserveSpecs.withResSearchAndSort(
	        memId,
	        type,
	        search,
	        sortBy,
	        sort,
	        statusFilter,
	        demNum
	    );

	    // DemonstrationReserve 페이징 조회
	    Page<DemonstrationReserve> resPage = demonstrationReserveRepository.findAll(spec, pageable);

	    // 관련 demNum 리스트 추출 (중복 제거)
	    List<Long> demNums = resPage.stream()
	        .map(res -> res.getDemonstration().getDemNum())
	        .distinct()
	        .toList();

	    // 이미지 리스트 일괄 조회 (demNum 기준)
	    List<DemonstrationImageDTO> images = demonstrationImageRepository.selectDemImageIn(demNums);
	    Map<Long, List<DemonstrationImageDTO>> demNumToImages = images.stream()
	        .collect(Collectors.groupingBy(DemonstrationImageDTO::getDemNum));

	    // 요청 상태 리스트 일괄 조회 (demRevNum 기준)
	    List<Long> demRevNums = resPage.stream()
	        .map(DemonstrationReserve::getDemRevNum)
	        .distinct()
	        .toList();

	    List<DemonstrationRequest> requests = demonstrationRequestRepository.findStateByDemRevNumIn(demRevNums);

	    // reserves에서 member.memId 리스트 추출 (중복 제거)
	    List<String> memIds = resPage.stream()
	        .map(reserve -> reserve.getMember().getMemId())
	        .distinct()
	        .toList();

	    // memberRepository에서 memId 리스트로 Member 리스트 조회
	    List<Member> members = memberRepository.findByMemIdIn(memIds);
	    Map<String, Member> memIdToMember = members.stream()
	        .collect(Collectors.toMap(Member::getMemId, m -> m));

	    // DTO 매핑
	    Page<DemonstrationListReserveDTO> dtoPage = resPage.map(res -> {
	        Demonstration dem = res.getDemonstration();

	        DemonstrationListReserveDTO dto = new DemonstrationListReserveDTO();

	        dto.setDemRevNum(res.getDemRevNum());
	        dto.setApplyAt(res.getApplyAt());
	        dto.setStartDate(res.getStartDate());
	        dto.setEndDate(res.getEndDate());
	        dto.setState(res.getState());
	        dto.setMemId(res.getMember().getMemId());
	        dto.setBItemNum(res.getBItemNum());

	        // Member 정보 가져오기
	        Member member = memIdToMember.get(res.getMember().getMemId());
	        if (member != null) {
	            if (member.getTeacher() != null) {
	                dto.setSchoolName(member.getTeacher().getSchoolName());
	            }
	            dto.setAddr(member.getAddr());
	            dto.setPhone(member.getPhone());
	        }

	        if (dem != null) {
	            dto.setDemName(dem.getDemName());
	            dto.setImageList(demNumToImages.getOrDefault(dem.getDemNum(), List.of()));
	        }

	        DemonstrationRequest req = requests.stream()
	            .filter(r -> r.getReserve().getDemRevNum().equals(res.getDemRevNum()))
	            .findFirst()
	            .orElse(null);

	        if (req != null) {
	            dto.setRequestDTO(new ResRequestDTO(req.getType(), req.getState()));
	        }

	        return dto;
	    });

	    return new PageResponseDTO<>(dtoPage);
	}





	
	// 실증 기업 신청목록 조회 기능 (검색도 같이 구현할 것임.) - 관리자용
	@Override
	public PageResponseDTO<DemonstrationListRegistrationDTO> getAllDemReg(String search, Integer pageCount) {
		if (pageCount == null || pageCount < 0) {
			pageCount = 0;
		}

		if (!StringUtils.hasText(search)) { // 검색어 입력이 없을 경우,
			Page<DemonstrationListRegistrationDTO> currentPage = demonstrationRegistrationRepository
					.selectPageDemReg(PageRequest.of(pageCount, 10, Sort.by("demRegNum").descending()));

			return new PageResponseDTO<DemonstrationListRegistrationDTO>(currentPage); // 페이지 DTO 객체 리턴
		}

		else { // 검색어를 입력 했을 경우,
			Page<DemonstrationListRegistrationDTO> currentPage = demonstrationRegistrationRepository
					.selectPageDemRegSearch(PageRequest.of(pageCount, 10, Sort.by("demRegNum").descending()), search);

			return new PageResponseDTO<DemonstrationListRegistrationDTO>(currentPage); // 페이지 DTO 객체 리턴
		}
	}

	// 물품 대여 현황 페이지에서 여러가지 항목들을 가져오는 기능
	@Override
	public PageResponseDTO<DemonstrationRentalListDTO> getAllDemRental(String memId,
			DemonstrationSearchDTO demonstrationSearchDTO) {

		Integer pageCount = demonstrationSearchDTO.getPageCount();
		String type = demonstrationSearchDTO.getType();
		String search = demonstrationSearchDTO.getSearch();
		String sortBy = demonstrationSearchDTO.getSortBy();
		String sort = demonstrationSearchDTO.getSort();
		String statusFilter = demonstrationSearchDTO.getStatusFilter();

		if (pageCount == null || pageCount < 0)
			pageCount = 0;
		if (!StringUtils.hasText(sortBy))
			sortBy = "applyAt";
		if (!StringUtils.hasText(sort))
			sort = "desc";

		Pageable pageable = PageRequest.of(pageCount, 10);

		Specification<DemonstrationReserve> spec = DemonstrationReserveSpecs.withSearchAndSort(memId, type, search,
				sortBy, sort, statusFilter);

		Page<DemonstrationReserve> reservePage = demonstrationReserveRepository.findAll(spec, pageable);

		// 1. reservePage에서 demNum 리스트 추출 (중복제거) (statae가 cancel인 목록 제거)
		List<Long> demNums = reservePage.stream().map(r -> r.getDemonstration().getDemNum()).distinct().toList();

		List<Long> demRevNums=reservePage.stream().map(r -> r.getDemRevNum()).distinct().toList();
		// 2. demonstration_registration 일괄 조회
		List<DemonstrationRegistration> regs = demonstrationRegistrationRepository
				.findByDemonstration_DemNumIn(demNums);

		// 3. memId 리스트 추출
		Set<String> memIds = regs.stream().map(r -> r.getMember().getMemId()).collect(Collectors.toSet());

		// 4. company 일괄 조회
		List<Company> companies = memberRepository.findCompanyByMemIdIn(memIds);

		// 5. Map 생성: demNum -> memId
		Map<Long, String> demNumToMemId = regs.stream()
				.collect(Collectors.toMap(r -> r.getDemonstration().getDemNum(), r -> r.getMember().getMemId()));

		// 6. Map 생성: memId -> companyName
		Map<String, String> memIdToCompanyName = companies.stream()
				.collect(Collectors.toMap(Company::getMemId, Company::getCompanyName));

		// 7. 이미지 리스트 일괄 조회
		List<DemonstrationImageDTO> images = demonstrationImageRepository.selectDemImageIn(demNums);

		
		// 8. Map 생성: demNum -> List<DemonstrationImageDTO>
		Map<Long, List<DemonstrationImageDTO>> demNumToImages = images.stream()
				.collect(Collectors.groupingBy(DemonstrationImageDTO::getDemNum));

		// 대여, 반납 상태 가져옴
		
		List<DemonstrationRequest> requests=demonstrationRequestRepository.findStateByDemRevNumIn(demRevNums);
		
		// 9. DTO 매핑
		Page<DemonstrationRentalListDTO> dtoPage = reservePage.map(reserve -> {
			Demonstration dem = reserve.getDemonstration();
			DemonstrationRentalListDTO dto = new DemonstrationRentalListDTO();

			dto.setDemNum(dem.getDemNum());
			dto.setDemName(dem.getDemName());
			dto.setItemNum(dem.getItemNum());
			dto.setBItemNum(reserve.getBItemNum());
			dto.setStartDate(reserve.getStartDate());
			dto.setEndDate(reserve.getEndDate());
			dto.setApplyAt(reserve.getApplyAt());
			dto.setState(reserve.getState());

			String regMemId = demNumToMemId.get(dem.getDemNum());
			String companyName = memIdToCompanyName.getOrDefault(regMemId, "회사명 없음");
			dto.setCompanyName(companyName);

			dto.setImageList(demNumToImages.getOrDefault(dem.getDemNum(), List.of()));
			
			DemonstrationRequest req = requests.stream()
				    .filter(r -> r.getReserve().getDemRevNum().equals(reserve.getDemRevNum()))
				    .findFirst()
				    .orElse(null);

				if (req != null) {
				    dto.setRequestType(req.getType());
				    dto.setReqState(req.getState());
				} else {
				    dto.setRequestType(null);
				    dto.setReqState(null);
				}
				
			return dto;
		});

		return new PageResponseDTO<>(dtoPage);
	}

	// 물품 대여 조회 페이지 연기 신청 및 반납 조기 신청
	@Override
	public void rentalDateChange(DemonstrationResRentalDTO demonstrationResRentalDTO) {
		// demonstrationRes테이블 endDate 변경
		demonstrationReserveRepository.updateDemResChangeDate(demonstrationResRentalDTO.getUpdatedStartDate(),
				demonstrationResRentalDTO.getUpdatedEndDate(), demonstrationResRentalDTO.getDemRevNum(),
				demonstrationResRentalDTO.getMemId());

		demonstrationTimeRepository.deleteDemTimes(demonstrationResRentalDTO.getOriginStartDate(),
				demonstrationResRentalDTO.getOriginEndDate());

		List<DemonstrationTime> dates = new ArrayList<>();
		for (LocalDate date = demonstrationResRentalDTO.getUpdatedStartDate(); !date
				.isAfter(demonstrationResRentalDTO.getUpdatedEndDate()); date = date.plusDays(1)) {
			dates.add(DemonstrationTime.builder().demDate(date)
					.demonstration(Demonstration.builder().demNum(demonstrationResRentalDTO.getDemNum()).build())
					.build());
		}

		demonstrationTimeRepository.saveAll(dates);
	}

	// 실증 장비신청 페이지 (실증 물품 리스트 목록)
	@Override
	public PageResponseDTO<DemonstrationPageListDTO> getAllDemList(Integer pageCount, String type, String search) {
		if (pageCount == null || pageCount < 0) {
			pageCount = 0;
		}
		Page<DemonstrationPageListDTO> currentPage; // 페이지 담을 객체
		if (type.equals("demName") && !search.equals("")) {
			currentPage = demonstrationRepository
					.selectPageDemName(PageRequest.of(pageCount, 4, Sort.by("demNum").descending()), search,DemonstrationState.CANCEL);
		} else if (type.equals("demMfr") && !search.equals("")) {
			currentPage = demonstrationRepository
					.selectPageDemMfr(PageRequest.of(pageCount, 4, Sort.by("demNum").descending()), search,DemonstrationState.CANCEL);
		} else {
			currentPage = demonstrationRepository
					.selectPageDem(PageRequest.of(pageCount, 4, Sort.by("demNum").descending()),DemonstrationState.CANCEL);
		}
		// list의 값을 넣지만 currentPage에도 setImage가 적용된다.
		// 얕은 참조 복사이기 때문에
		List<DemonstrationPageListDTO> list = currentPage.getContent();

		// 반복문으로 리포지토리를 여러번 호출하는 문제 해결위해
		// 만든 demNumList
		List<Long> demNumList = new ArrayList<>();
		for (DemonstrationPageListDTO dto : list) {
			demNumList.add(dto.getDemNum());
		}
		// 같은 demNum을 갖는 이미지가 하나의 객체에 다 들어있음
		List<DemonstrationImageDTO> allImages = demonstrationImageRepository.selectDemImageIn(demNumList);
		for (DemonstrationPageListDTO dto : list) {
			List<DemonstrationImageDTO> imageList = new ArrayList<>();
			for (DemonstrationImageDTO img : allImages) {
				// 받아온 이미지 객체 배열과 페이지의 dto와
				// demNum 비교시, 같은 경우 해당 객체에
				// set처리
				if (img.getDemNum().equals(dto.getDemNum())) {
					imageList.add(img);
				}
			}
			dto.setImageList(imageList);
		}

		return new PageResponseDTO<DemonstrationPageListDTO>(currentPage);
	}

	// 해당 상품의 예약 정보를 가져오는 기능(실증 장비 신청 페이지에서 대여가능 / 예약 마감 표기 할거임)
	@Override
	public List<DemonstrationTimeResDTO> checkReservationState(DemonstrationTimeReqDTO demonstrationTimeReqDTO) {
		// 시작 날짜와 끝 날짜, 실증 번호를 통해 예약 날짜 리스트를 불러와 리턴
		List<DemonstrationTimeResDTO> DateList = demonstrationTimeRepository.findReservedDates(
				demonstrationTimeReqDTO.getStartDate(), demonstrationTimeReqDTO.getEndDate(),
				demonstrationTimeReqDTO.getDemNum());
		return DateList;
	}

	// 현재 회원의 예약 정보를 제외하기 위해 해당 상품의 예약 정보를 가져오는 기능
	@Override
	public List<DemonstrationTimeResDTO> checkReservationStateExcept(DemonstrationTimeReqDTO demonstrationTimeReqDTO,
			String memId) {
		// 현재 회원의 예약을 제외한 해당 물품의 예약 시작 날짜와 끝 날짜를 받아옴
		DemonstrationTimeReqDTO dto = demonstrationReserveRepository.getResDate(demonstrationTimeReqDTO.getDemNum(),
				memId, DemonstrationState.CANCEL);
		List<DemonstrationTimeResDTO> DateList = demonstrationTimeRepository.findReservedDates(dto.getStartDate(),
				dto.getEndDate(), demonstrationTimeReqDTO.getDemNum());
		return DateList;
	}

	// 실증 장비 신청 상세 페이지
	@Override
	public DemonstrationDetailDTO getDemDetailList(Long demNum) {
		// 실증 장비 번호로 장비 상세 정보를 받아와 리턴
		DemonstrationDetailDTO detailDem = new DemonstrationDetailDTO();
		detailDem = demonstrationRepository.selectPageDetailDem(demNum);
		detailDem.setImageList(demonstrationImageRepository.selectDemImage(demNum));
		return detailDem;
	}

	// 실증 신청 상세 페이지에서 예약 신청하기 클릭시, 예약 정보 저장
	@Override
	@Transactional
	public void demonstrationReservation(DemonstrationReservationDTO demonstrationReservationDTO, String memId) {
		// 선택한 실증 상품의 예약된 상태를 불러오기 위해 사용한 dto
		Long beforeItemNum = demonstrationRepository.selectItemNum(demonstrationReservationDTO.getDemNum());
		DemonstrationTimeReqDTO demonstrationTimeReqDTO = new DemonstrationTimeReqDTO();
		demonstrationTimeReqDTO.setDemNum(demonstrationReservationDTO.getDemNum());
		demonstrationTimeReqDTO.setStartDate(demonstrationReservationDTO.getStartDate());
		demonstrationTimeReqDTO.setEndDate(demonstrationReservationDTO.getEndDate());
		List<DemonstrationTimeResDTO> ResState = checkReservationState(demonstrationTimeReqDTO);
		int result = demonstrationRepository.updateItemNum(demonstrationReservationDTO.getItemNum(),
				demonstrationReservationDTO.getDemNum());
		Member member = memberRepository.findById(memId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다"));
		if (ResState == null || ResState.isEmpty()) {
			DemonstrationReserve demonstrationReserve = DemonstrationReserve.builder().applyAt(LocalDate.now())
					.startDate(demonstrationReservationDTO.getStartDate())
					.endDate(demonstrationReservationDTO.getEndDate()).state(DemonstrationState.WAIT)
					.demonstration(Demonstration.builder().demNum(demonstrationReservationDTO.getDemNum()).build())
					.member(member).bItemNum(beforeItemNum - demonstrationReservationDTO.getItemNum()).build();
			demonstrationReserveRepository.save(demonstrationReserve);

			// 실증 신청 시 예약된 날짜도 추가되야 하므로
			// demTime 테이블에 예약된 시간 추가
			List<DemonstrationTime> demonstrationTimeList = new ArrayList<>();
			for (LocalDate date = demonstrationReservationDTO.getStartDate(); !date
					.isAfter(demonstrationReservationDTO.getEndDate()); date = date.plusDays(1)) {
				DemonstrationTime demonstrationTime = DemonstrationTime.builder().demDate(date)
						.demonstration(Demonstration.builder().demNum(demonstrationReservationDTO.getDemNum()).build())
						.build();
				demonstrationTimeList.add(demonstrationTime);
			} // 변경 전 날짜로 부터 변경 후 까지의 날짜의 예약 상태 추가
				// time 리스트를 저장
			demonstrationTimeRepository.saveAll(demonstrationTimeList);

		} else {
			System.out.println("예약된 날짜가 겹쳐 있습니다.");
		}
	}

	// 물품대여 페이지에서 예약 취소 버튼 클릭 시, 상태값을 cancel로 바꿈
	@Override
	public void demonstrationReservationCancel(List<Long> demNum, String memId) {
		// 불러온 아이디와 실증 번호를 통해 신청 번호를 받아온 후,
		List<DemonstrationReserve> demonstrationReserve = demonstrationReserveRepository.findDemRevNum(memId, demNum,
				DemonstrationState.CANCEL);

		if (demonstrationReserve == null) {
			System.out.println("예약 정보가 없습니다.");
			return;
		}
		Long updateItemNum;
		for (Long num : demNum) {
			updateItemNum = demonstrationReserveRepository.getBItemNum(num, memId, DemonstrationState.CANCEL); // 예약 취소한
																												// 갯수+기존의
																												// 갯수
			demonstrationRepository.updateItemNum(updateItemNum, num);
		}

		// 신청 번호를 통한 상태 업데이트 (cancel)
		demonstrationReserveRepository.updateDemResChangeState(DemonstrationState.CANCEL, memId, demNum);
		// demonstartionTime테이블에 있는 예약 정보도 삭제

		for (DemonstrationReserve res : demonstrationReserve) {
			List<LocalDate> deleteTimeList = new ArrayList<>();
			for (LocalDate date = res.getStartDate(); !date.isAfter(res.getEndDate()); date = date.plusDays(1)) {
				deleteTimeList.add(date);
			}
			// 저장되어 있던 시작 번호와 끝 번호를 가져와
			// time테이블의 예약 정보도 삭제
			demonstrationTimeRepository.deleteDemTimeList(deleteTimeList);
		}
	}

	@Override
	@Transactional
	public void demonstrationReservationCancels(List<Long> demNums, List<String> memIds) {
	    // 해당 memIds와 demNums 조건에 맞는 예약 목록 조회
	    List<DemonstrationReserve> demonstrationReserves = demonstrationReserveRepository.findDemRevNums(memIds, demNums,
	            DemonstrationState.CANCEL);

	    if (demonstrationReserves.isEmpty()) {
	        System.out.println("예약 정보가 없습니다.");
	        return;
	    }

	    // 각 demNum별로 취소된 갯수 + 기존 갯수를 업데이트 (batch 처리 메서드로 교체 권장)
	    for (Long demNum : demNums) {
	        Long updateItemNum = demonstrationReserveRepository.getBItemNumBatch(demNum, memIds, DemonstrationState.CANCEL);
	        demonstrationRepository.updateItemNum(updateItemNum, demNum);
	    }

	    // 상태 업데이트 (cancel) - batch 처리용 메서드
	    demonstrationReserveRepository.updateDemResChangeStateBatch(DemonstrationState.CANCEL, memIds, demNums);

	    // 데모 예약 시간 삭제
	    for (DemonstrationReserve res : demonstrationReserves) {
	        List<LocalDate> deleteTimeList = new ArrayList<>();
	        for (LocalDate date = res.getStartDate(); !date.isAfter(res.getEndDate()); date = date.plusDays(1)) {
	            deleteTimeList.add(date);
	        }
	        demonstrationTimeRepository.deleteDemTimeList(deleteTimeList);
	    }
	}


	
	
	// 실증 신청 페이지에서 예약 변경하기 클릭 시, 예약 정보 변경
	@Override
	public void demonstrationReservationChange(DemonstrationReservationDTO demonstrationReservationDTO, String memId) {
		// 기존 예약 취소
		demonstrationReservationCancel(Collections.singletonList(demonstrationReservationDTO.getDemNum()), memId);
		// 새로운 예약 추가
		demonstrationReservation(demonstrationReservationDTO, memId);
	}

	// 실증 상품 등록 페이지에서 실증 상품 등록하는 기능
	@Override
	public void addDemonstration(DemonstrationFormReqDTO demonstrationFormDTO, List<MultipartFile> imageList,
			String memId) {
		Demonstration demonstration = Demonstration.builder().demName(demonstrationFormDTO.getDemName())
				.demInfo(demonstrationFormDTO.getDemInfo()).demMfr(demonstrationFormDTO.getDemMfr())
				.itemNum(demonstrationFormDTO.getItemNum()).build();

		// 실증 물품 등록
		demonstrationRepository.save(demonstration);
		Long demNum = demonstration.getDemNum();

		// System.out.println(memId);
		Member member = memberRepository.findById(memId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다"));
		DemonstrationRegistration demonstrationRegistration = DemonstrationRegistration.builder()
				.regDate(LocalDate.now()).expDate(demonstrationFormDTO.getExpDate()).state(DemonstrationState.WAIT)
				.demonstration(Demonstration.builder().demNum(demNum).build()).member(member).build();

		// 실증 등록
		demonstrationRegistrationRepository.save(demonstrationRegistration);

		// 폴더에 이미지 저장 (demImages라는 폴더에)
		List<Object> files = fileUtil.saveFiles(imageList, "demImages");

		Integer mainIndex = demonstrationFormDTO.getMainImageIndex(); // ex) 0, 1, 2 중 하나

		for (int i = 0; i < files.size(); i++) {
			Object obj = files.get(i);
			if (obj instanceof Map) {
				Map<String, String> map = (Map<String, String>) obj;

				boolean isMain = (mainIndex != null && mainIndex == i); // 현재 인덱스가 mainIndex면 true

				DemonstrationImage demonstrationImage = DemonstrationImage.builder().imageName(map.get("originalName"))
						.imageUrl(map.get("filePath")).demonstration(Demonstration.builder().demNum(demNum).build())
						.isMain(isMain).build();

				demonstrationImageRepository.save(demonstrationImage);
			}
		}

	}

	@Override
	@Transactional
	public void updateDemonstration(DemonstrationFormReqDTO demonstrationFormDTO, List<MultipartFile> imageList,
			String memId) {
		// 실증 상품 정보 업데이트
		demonstrationRepository.updateDem(demonstrationFormDTO.getDemName(), demonstrationFormDTO.getDemMfr(),
				demonstrationFormDTO.getItemNum(), demonstrationFormDTO.getDemInfo(), demonstrationFormDTO.getDemNum());

		// 반납 예정일 수정
		demonstrationRegistrationRepository.updateDemRegChangeExpDate(demonstrationFormDTO.getExpDate(),
				demonstrationFormDTO.getDemNum(), memId);

		// 기존 상품 이미지 불러옴(폴더에서 이미지 삭제 위해)
		List<DemonstrationImageDTO> deleteImageList = demonstrationImageRepository
				.selectDemImage(demonstrationFormDTO.getDemNum());
		List<String> filePaths = new ArrayList<>();
		for (DemonstrationImageDTO dto : deleteImageList) {
			String path = dto.getImageUrl();
			String s_path = "s_" + dto.getImageUrl();
			filePaths.add(path);
			filePaths.add(s_path);
		}

		// 폴더에서 이미지 삭제
		fileUtil.deleteFiles(filePaths);

		// 기존 상품 이미지 삭제 후,
		demonstrationImageRepository.deleteDemNumImage(demonstrationFormDTO.getDemNum());

		if (imageList != null && !imageList.isEmpty()) {
			List<Object> files = fileUtil.saveFiles(imageList, "demImages");

			int mainIndex = demonstrationFormDTO.getMainImageIndex() != null ? demonstrationFormDTO.getMainImageIndex()
					: 0;

			for (int i = 0; i < files.size(); i++) {
				Object obj = files.get(i);
				if (obj instanceof Map) {
					Map<String, String> map = (Map<String, String>) obj;
					DemonstrationImage demonstrationimage = DemonstrationImage.builder()
							.imageName(map.get("originalName")).imageUrl(map.get("filePath")).isMain(i == mainIndex) // mainImageIndex와
																														// 비교해서
																														// true/false
																														// 설정
							.demonstration(Demonstration.builder().demNum(demonstrationFormDTO.getDemNum()).build())
							.build();
					demonstrationImageRepository.save(demonstrationimage);
				}
			}
		}
	}

	// 실증 번호를 받아서 실증 상품을 삭제하는 기능
	@Override
	public void deleteDemonstration(Long demNum,String memId) {
		demonstrationRegistrationRepository.updateDemRegChangeState(DemonstrationState.CANCEL,memId,demNum);
		List<Long> demNums=new ArrayList<>();
		demNums.add(demNum);
		List<String>rentalMemId=new ArrayList<>();
		rentalMemId=demonstrationReserveRepository.getResMemId(demNum,DemonstrationState.CANCEL);
		System.out.println(rentalMemId);
		demonstrationReservationCancels(demNums, rentalMemId);
	}

	// 실증 번호를 받아서 실증 상품의 정보를 받아오는 기능
	@Override
	public DemonstrationFormResDTO selectOne(Long demNum) {
		Demonstration entity = demonstrationRepository.findById(demNum)
				.orElseThrow(() -> new RuntimeException("해당 번호의 실증 정보가 없습니다: " + demNum));
		DemonstrationFormResDTO dto = modelMapper.map(entity, DemonstrationFormResDTO.class);
		List<DemonstrationImageDTO> imageDtoList = demonstrationImageRepository.selectDemImage(demNum);
		// 각 리스트 분리해서 dto에 세팅
		for (DemonstrationImageDTO imageDto : imageDtoList) {
			dto.getImageUrlList().add(imageDto.getImageUrl());
			dto.getImageNameList().add(imageDto.getImageName());
			dto.getIsMain().add(String.valueOf(imageDto.getIsMain())); // boolean을 string으로
		}

		dto.setExpDate(demonstrationRegistrationRepository.selectDemRegExpDate(demNum));

		return dto;
	}

	// 물품 상세정보 페이지에서 현재 회원이 해당 물품에 예약이 되어있을 경우를 나타내는 기능
	@Override
	public Boolean checkRes(Long demNum, String memId) {
		Boolean bool = demonstrationReserveRepository.checkRes(demNum, memId, DemonstrationState.CANCEL).orElse(false);
		return bool;
	}

	// 물품 대여 리스트 페이지에서 연기 신청, 반납 신청 하는 기능
	@Override
	public void addRequest(ResRequestDTO resRequestDTO, String memId) {
		DemonstrationReserve reserve = demonstrationReserveRepository.getRev(resRequestDTO.getDemNum(), memId,
				DemonstrationState.CANCEL);
		DemonstrationRequest request = new DemonstrationRequest();
		request.setApplyAt(LocalDate.now());
		request.setReserve(reserve);
		request.setState(DemonstrationState.WAIT);
		request.setType(resRequestDTO.getType());
		demonstrationRequestRepository.save(request);
	}

	// 실증 등록한 기업의 물품 리스트를 보여주는 기능
	@Override
	public PageResponseDTO<DemonstrationBorrowListDTO> AllgetBorrow(String memId, DemonstrationSearchDTO demonstrationSearchDTO) {

	    Integer pageCount = demonstrationSearchDTO.getPageCount();
	    String type = demonstrationSearchDTO.getType();
	    String search = demonstrationSearchDTO.getSearch();
	    String sortBy = demonstrationSearchDTO.getSortBy();
	    String sort = demonstrationSearchDTO.getSort();
	    String statusFilter = demonstrationSearchDTO.getStatusFilter();

	    if (pageCount == null || pageCount < 0) {
	        pageCount = 0;
	    }

	    Sort.Direction direction = "desc".equalsIgnoreCase(sort) ? Sort.Direction.DESC : Sort.Direction.ASC;
	    Sort sortObj;
	    if ("expDate".equalsIgnoreCase(sortBy)) {
	        sortObj = Sort.by(direction, "expDate");
	    } else {
	        sortObj = Sort.by(direction, "regDate");
	    }

	    Pageable pageable = PageRequest.of(pageCount, 10, sortObj);

	    Specification<DemonstrationRegistration> spec = DemonstrationReservationSpecs.withSearchAndSort(
	            memId, type, search, sortBy, sort, statusFilter);

	    Page<DemonstrationRegistration> entityPage = demonstrationRegistrationRepository.findAll(spec, pageable);
	    
	   // System.out.println(entityPage.getContent());
	    // demNum 리스트 추출 (중복 제거)
	    List<Long> demNums = entityPage.stream()
	            .map(r -> r.getDemonstration().getDemNum())
	            .distinct()
	            .toList();

	    // 이미지 리스트 일괄 조회
	    List<DemonstrationImageDTO> images = demonstrationImageRepository.selectDemImageIn(demNums);

	    // demNum -> 이미지 리스트 매핑
	    Map<Long, List<DemonstrationImageDTO>> demNumToImages = images.stream()
	            .collect(Collectors.groupingBy(DemonstrationImageDTO::getDemNum));

	    // DTO 매핑
	    Page<DemonstrationBorrowListDTO> dtoPage = entityPage.map(reg -> {
	        Demonstration dem = reg.getDemonstration();

	        DemonstrationBorrowListDTO dto = new DemonstrationBorrowListDTO(
	                dem.getDemNum(),
	                dem.getDemName(),
	                dem.getItemNum(),
	                dem.getDemMfr(),
	                reg.getExpDate(),
	                reg.getRegDate(),
	                reg.getState()
	        );

	        dto.setImageList(demNumToImages.getOrDefault(dem.getDemNum(), List.of()));

	        return dto;
	    });

	    return new PageResponseDTO<>(dtoPage);
	}


}
