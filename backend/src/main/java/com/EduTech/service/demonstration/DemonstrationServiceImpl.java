package com.EduTech.service.demonstration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.demonstration.DemonstrationBorrowListDTO;
import com.EduTech.dto.demonstration.DemonstrationDetailDTO;
import com.EduTech.dto.demonstration.DemonstrationFormReqDTO;
import com.EduTech.dto.demonstration.DemonstrationFormResDTO;
import com.EduTech.dto.demonstration.DemonstrationImageDTO;
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
import com.EduTech.entity.demonstration.RequestType;
import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationSpecs;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRequestRepository;
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

	private final DemonstrationReserveRepository demonstrationReserveRepository;
	private final DemonstrationRegistrationRepository demonstrationRegistrationRepository;
	private final DemonstrationRepository demonstrationRepository;
	private final DemonstrationTimeRepository demonstrationTimeRepository;
	private final DemonstrationImageRepository demonstrationImageRepository;
	private final FileUtil fileUtil;
	private final DemonstrationRequestRepository demonstrationRequestRepository;
	private final MemberRepository memberRepository;
	private final ModelMapper modelMapper;

	// 실증 물품 대여 조회
	@Override
	public PageResponseDTO<DemonstrationListReserveDTO> getAllDemResRental(DemonstrationSearchDTO searchDTO,
			String memId) {
		int pageCount = searchDTO.getPageCount() != null && searchDTO.getPageCount() >= 0 ? searchDTO.getPageCount()
				: 0;
		String sortBy = searchDTO.getSortBy() != null && !searchDTO.getSortBy().isEmpty() ? searchDTO.getSortBy()
				: "applyAt";
		String sort = searchDTO.getSort() != null && !searchDTO.getSort().isEmpty() ? searchDTO.getSort() : "desc";

		Pageable pageable = PageRequest.of(pageCount, 10);

		Specification<DemonstrationReserve> spec = DemonstrationReserveSpecs.withResSearchAndSort(memId,
				searchDTO.getType(), searchDTO.getSearch(), sortBy, sort, searchDTO.getStatusFilter(),
				searchDTO.getDemNum());

		Page<DemonstrationReserve> resPage = demonstrationReserveRepository.findAll(spec, pageable);

		Map<String, Member> memMap = memberRepository
				.findByMemIdIn(resPage.stream().map(r -> r.getMember().getMemId()).distinct().toList()).stream()
				.collect(Collectors.toMap(Member::getMemId, m -> m));

		List<DemonstrationListReserveDTO> dtoList = resPage.stream().map(res -> {
			Member m = memMap.get(res.getMember().getMemId());
			Demonstration d = res.getDemonstration();

			DemonstrationListReserveDTO dto = new DemonstrationListReserveDTO();
			dto.setDemRevNum(res.getDemRevNum());
			dto.setApplyAt(res.getApplyAt());
			dto.setStartDate(res.getStartDate());
			dto.setEndDate(res.getEndDate());
			dto.setState(res.getState());
			dto.setMemId(res.getMember().getMemId());
			dto.setBItemNum(res.getBItemNum());
			dto.setDemName(d != null ? d.getDemName() : null);

			if (m != null) {
				dto.setAddr(m.getAddr());
				dto.setAddrDetail(m.getAddrDetail());
				dto.setPhone(m.getPhone());
				if (m.getTeacher() != null)
					dto.setSchoolName(m.getTeacher().getSchoolName());
			}
			return dto;
		}).toList();

		// List와 페이징 정보로 PageResponseDTO 생성
		return new PageResponseDTO<>(dtoList, resPage.getTotalPages(), resPage.getNumber(), resPage.getTotalElements());
	}

	// 교사가 빌린 물품 내역 페이지 조회
	@Override
	public PageResponseDTO<DemonstrationRentalListDTO> getAllDemRental(String memId, DemonstrationSearchDTO searchDTO) {
		// 페이징 & 정렬 기본값
		int pageCount = searchDTO.getPageCount() != null && searchDTO.getPageCount() >= 0 ? searchDTO.getPageCount()
				: 0;
		String sortBy = searchDTO.getSortBy() != null && !searchDTO.getSortBy().isEmpty() ? searchDTO.getSortBy()
				: "applyAt";
		String sort = searchDTO.getSort() != null && !searchDTO.getSort().isEmpty() ? searchDTO.getSort() : "desc";

		Pageable pageable = PageRequest.of(pageCount, 10);

		Specification<DemonstrationReserve> spec = DemonstrationReserveSpecs.withSearchAndSort(memId,
				searchDTO.getType(), searchDTO.getSearch(), sortBy, sort, searchDTO.getStatusFilter());

		Page<DemonstrationReserve> reservePage = demonstrationReserveRepository.findAll(spec, pageable);

		// demNum / demRevNum / memId 리스트 추출
		List<Long> demNums = reservePage.stream().map(r -> r.getDemonstration().getDemNum()).distinct().toList();
		List<Long> demRevNums = reservePage.stream().map(DemonstrationReserve::getDemRevNum).distinct().toList();

		// registration 조회 및 Map 생성
		List<DemonstrationRegistration> regs = demonstrationRegistrationRepository
				.findByDemonstration_DemNumIn(demNums);
		Map<Long, String> demNumToMemId = regs.stream()
				.collect(Collectors.toMap(r -> r.getDemonstration().getDemNum(), r -> r.getMember().getMemId()));
		Set<String> memIds = regs.stream().map(r -> r.getMember().getMemId()).collect(Collectors.toSet());

		// company 조회 및 Map 생성
		Map<String, String> memIdToCompanyName = memberRepository.findCompanyByMemIdIn(memIds).stream()
				.collect(Collectors.toMap(Company::getMemId, Company::getCompanyName));

		// 이미지 조회 및 Map
		Map<Long, List<DemonstrationImageDTO>> demNumToImages = demonstrationImageRepository.selectDemImageIn(demNums)
				.stream().collect(Collectors.groupingBy(DemonstrationImageDTO::getDemNum));

		// 요청 리스트 조회 및 Map
		Map<Long, List<DemonstrationRequest>> demRevNumToRequests = demonstrationRequestRepository
				.findStateByDemRevNumIn(demRevNums).stream()
				.collect(Collectors.groupingBy(r -> r.getReserve().getDemRevNum()));

		// DTO 변환
		List<DemonstrationRentalListDTO> dtoList = reservePage.stream().map(reserve -> {
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
			dto.setDemRevNum(reserve.getDemRevNum());
			String regMemId = demNumToMemId.get(dem.getDemNum());
			dto.setCompanyName(memIdToCompanyName.getOrDefault(regMemId, "회사명 없음"));
			dto.setImageList(demNumToImages.getOrDefault(dem.getDemNum(), List.of()));

			List<DemonstrationRequest> reqList = demRevNumToRequests.getOrDefault(reserve.getDemRevNum(), List.of());
			dto.setRequestType(reqList.isEmpty() ? null : reqList.stream().map(DemonstrationRequest::getType).toList());
			dto.setReqState(reqList.isEmpty() ? null : reqList.stream().map(DemonstrationRequest::getState).toList());

			return dto;
		}).toList();

		return new PageResponseDTO<>(dtoList, reservePage.getTotalPages(), reservePage.getNumber(),
				reservePage.getTotalElements());
	}
	
	// 해당 상품의 예약 정보를 가져오는 기능(실증 장비 신청 페이지에서 대여가능 / 예약 마감 표기 할거임)
		@Override
		public List<LocalDate> checkReservationState(DemonstrationTimeReqDTO demonstrationTimeReqDTO) {
		    // 시작 날짜와 끝 날짜, 실증 번호를 통해 예약 날짜 리스트를 불러옴(wait와 accept 상태의 경우에만 time테이블에 저장되므로 상태값 안넣어도 됨)
			System.out.println("------------------------------------");
			System.out.println(demonstrationTimeReqDTO);
		    List<DemonstrationTimeResDTO> dateList = demonstrationTimeRepository.findReservedDates(
		            demonstrationTimeReqDTO.getStartDate(),
		            demonstrationTimeReqDTO.getEndDate(),
		            demonstrationTimeReqDTO.getDemNum()
		    );
		    System.out.println(dateList);
		    // DTO 리스트에서 LocalDate만 추출
		    return dateList.stream()
		                   .map(DemonstrationTimeResDTO::getDemDate)
		                   .collect(Collectors.toList());
		}

		// 현재 회원의 예약 정보를 제외한 상품 예약 정보 가져오기
		@Override
		public List<LocalDate> checkReservationStateExcept(DemonstrationTimeReqDTO dto, String memId) {
		    // 조회할 상태 (대기, 수락)
		    List<DemonstrationState> states = Arrays.asList(DemonstrationState.ACCEPT, DemonstrationState.WAIT);

		    // 1. 현재 달 범위 내 모든 예약 날짜 가져오기 (모든 회원)
		    List<DemonstrationTimeResDTO> reservedDates = demonstrationTimeRepository.findReservedDates(
		            dto.getStartDate(),
		            dto.getEndDate(),
		            dto.getDemNum()
		    );
		    System.out.println(reservedDates);
		    // 2. 로그인한 회원의 예약 정보 가져오기
		    List<DemonstrationTimeReqDTO> memberReservations = demonstrationReserveRepository.getResDate(
		            dto.getDemNum(),
		            memId,
		            states
		    );
		    // 로그인 회원 예약 날짜만 추출
		    List<LocalDate> memberReservedDates = memberReservations.stream()
		            .flatMap(r -> r.getStartDate().datesUntil(r.getEndDate().plusDays(1)))
		            .collect(Collectors.toList());

		    // 3. 현재 달 예약 날짜에서 로그인 회원 예약 제외
		    List<LocalDate> availableDates = reservedDates.stream()
		            .map(DemonstrationTimeResDTO::getDemDate)
		            .filter(d -> !memberReservedDates.contains(d))
		            .collect(Collectors.toList());

		    return availableDates;
		}


	
	// 실증 장비신청 페이지 (실증 물품 리스트 목록)
	@Override
	public PageResponseDTO<DemonstrationPageListDTO> getAllDemList(Integer pageCount, String type, String search,
			String sortType) {
		if (pageCount == null || pageCount < 0)
			pageCount = 0;
		Sort sort = "asc".equalsIgnoreCase(sortType) ? Sort.by(Sort.Direction.ASC, "reg.expDate")
				: Sort.by(Sort.Direction.DESC, "reg.expDate");

		Page<DemonstrationPageListDTO> currentPage;

		if ("demName".equals(type) && !search.isEmpty()) {
			currentPage = demonstrationRepository.selectPageDemName(PageRequest.of(pageCount, 4, sort), search,
					DemonstrationState.ACCEPT);
		} else if ("demMfr".equals(type) && !search.isEmpty()) {
			currentPage = demonstrationRepository.selectPageDemMfr(PageRequest.of(pageCount, 4, sort), search,
					DemonstrationState.ACCEPT);
		} else if ("companyName".equals(type) && !search.isEmpty()) {
			currentPage = demonstrationRepository.selectPageDemCompanyName(PageRequest.of(pageCount, 4, sort), search,
					DemonstrationState.ACCEPT);
		} else {
			currentPage = demonstrationRepository.selectPageDem(PageRequest.of(pageCount, 4, sort),
					DemonstrationState.ACCEPT);
		}

		// list의 값을 넣지만 currentPage에도 setImage가 적용된다.
		// 얕은 참조 복사이기 때문에
		List<DemonstrationPageListDTO> list = currentPage.getContent();

		// 반복문으로 리포지토리를 여러번 호출하는 문제 해결위해 만든 demNumList
		List<Long> demNumList = list.stream().map(DemonstrationPageListDTO::getDemNum).toList();

		// 같은 demNum을 갖는 이미지가 하나의 객체에 다 들어있음
		List<DemonstrationImageDTO> allImages = demonstrationImageRepository.selectDemImageIn(demNumList);

		// 각 DTO에 이미지 리스트 set
		list.forEach(dto -> dto
				.setImageList(allImages.stream().filter(img -> img.getDemNum().equals(dto.getDemNum())).toList()));

		return new PageResponseDTO<>(list, currentPage.getTotalPages(), currentPage.getNumber(),
				currentPage.getTotalElements());
	}

	


	// 실증 장비 신청 상세 페이지
	@Override
	public DemonstrationDetailDTO getDemDetailList(Long demNum) {
		// 실증 장비 번호로 장비 상세 정보를 받아와 리턴
		DemonstrationDetailDTO detailDem = new DemonstrationDetailDTO();
		detailDem = demonstrationRepository.selectPageDetailDem(demNum);
		detailDem.setImageList(demonstrationImageRepository.selectDemImageIn(List.of(demNum)));
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
		
		List<LocalDate> ResState = checkReservationState(demonstrationTimeReqDTO);
		
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

	// 물품대여 페이지에서 예약 취소 버튼 클릭 시, 상태값을 cancel로 바꿈 관리자도 포함
	// 예약 변경시에도 호출
	@Override
	@Transactional
	public void demonstrationReservationCancel(List<Long> demRevNum) {
	    System.out.println(demRevNum);
	    List<DemonstrationState> state=new ArrayList<>();
	    state.add(DemonstrationState.ACCEPT);
	    state.add(DemonstrationState.WAIT);
	    state.add(DemonstrationState.EXPIRED);
	    List<DemonstrationReserve> demonstrationReserve = demonstrationReserveRepository.findDemRevNum(demRevNum,
	            state);
	    if (demonstrationReserve == null) {
	        System.out.println("예약 정보가 없습니다.");
	        return;
	    }
	    
	    for (DemonstrationReserve res : demonstrationReserve) {
	        String memId = res.getMember().getMemId();
	        Long demNum = res.getDemonstration().getDemNum();
	        
	        // 현재 itemNum 가져오기
	        Long currentItemNum = demonstrationRepository.selectItemNum(demNum);
	        
	        // 해당 회원의 ACCEPT, WAIT 상태 예약 수량 가져오기
	        Long bItemNum = demonstrationReserveRepository.findBitemNum(demNum, memId, 
	                Arrays.asList(DemonstrationState.ACCEPT, DemonstrationState.WAIT));
	        
	        // itemNum 업데이트: 현재 itemNum + 취소할 예약 수량
	        Long updateItemNum = currentItemNum + res.getBItemNum();
	        demonstrationRepository.updateItemNum(updateItemNum, demNum);
	    }

	    // 신청 번호를 통한 상태 업데이트 (cancel)
	    demonstrationReserveRepository.updateDemResChangeState(DemonstrationState.CANCEL, demRevNum);
	    
	    // demonstartionTime테이블에 있는 예약 정보도 삭제
	    for (DemonstrationReserve res : demonstrationReserve) {
	        List<LocalDate> deleteTimeList = new ArrayList<>();
	        for (LocalDate date = res.getStartDate(); !date.isAfter(res.getEndDate()); date = date.plusDays(1)) {
	            deleteTimeList.add(date);
	        }
	        // 저장되어 있던 시작 번호와 끝 번호를 가져와
	        // time테이블의 예약 정보도 삭제
	        demonstrationTimeRepository.deleteTimeDemNum(deleteTimeList,res.getDemonstration().getDemNum());
	    }
	}


	// 실증 신청 페이지에서 예약 변경하기 클릭 시, 예약 정보 변경
	@Override
	@Transactional
	public void demonstrationReservationChange(DemonstrationReservationDTO demonstrationReservationDTO, String memId) {
	    System.out.println("예약 변경 시작 - demRevNum: " + demonstrationReservationDTO.getDemRevNum());
	    
	    List<DemonstrationState> state = new ArrayList<>();
	    state.add(DemonstrationState.WAIT);
	    
	    // 1. 기존 예약 조회
	    List<DemonstrationReserve> demonstrationReserve = demonstrationReserveRepository.findDemRevNum(
	            Arrays.asList(demonstrationReservationDTO.getDemRevNum()), state);

	    if (demonstrationReserve == null || demonstrationReserve.isEmpty()) {
	        System.out.println("변경할 예약을 찾을 수 없습니다. demRevNum: " + demonstrationReservationDTO.getDemRevNum());
	        throw new RuntimeException("변경할 예약을 찾을 수 없습니다.");
	    }

	    DemonstrationReserve res = demonstrationReserve.get(0);
	    System.out.println("기존 예약 정보:");
	    System.out.println("- 시작일: " + res.getStartDate());
	    System.out.println("- 종료일: " + res.getEndDate());
	    System.out.println("- 수량: " + res.getBItemNum());
	    System.out.println("- 현재 상태: " + res.getState());

	    try {
	        // 2. itemNum 복구 (취소할 예약 수량만큼 증가)
	        Long currentItemNum = demonstrationRepository.selectItemNum(res.getDemonstration().getDemNum());
	        Long restoredItemNum = currentItemNum + res.getBItemNum();
	        System.out.println("itemNum 복구: " + currentItemNum + " -> " + restoredItemNum);
	        
	        int itemUpdateResult = demonstrationRepository.updateItemNum(restoredItemNum, res.getDemonstration().getDemNum());
	        System.out.println("itemNum 업데이트 결과: " + itemUpdateResult);

	        // 3. 예약 시간 삭제 (상태 변경 전에 먼저 삭제)
	        List<LocalDate> deleteTimeList = new ArrayList<>();
	        for (LocalDate date = res.getStartDate(); !date.isAfter(res.getEndDate()); date = date.plusDays(1)) {
	            deleteTimeList.add(date);
	        }
	        
	        if (!deleteTimeList.isEmpty()) {
	            System.out.println("삭제할 시간 목록: " + deleteTimeList);
	           demonstrationTimeRepository.deleteDemTimeList(deleteTimeList);
	        }

	        // 4. 예약 상태를 CANCEL로 변경
	        int stateUpdateResult = demonstrationReserveRepository.updateDemResChangeState(
	                DemonstrationState.CANCEL,
	                Arrays.asList(demonstrationReservationDTO.getDemRevNum()));
	        System.out.println("예약 상태 변경 결과: " + stateUpdateResult);

	        // 5. 기존 예약 메서드 재사용 (프론트엔드와 동일한 로직)
	        System.out.println("새로운 예약 생성 시작");
	        demonstrationReservation(demonstrationReservationDTO, memId);
	        System.out.println("예약 변경 완료");

	    } catch (Exception e) {
	        System.err.println("예약 변경 중 오류 발생: " + e.getMessage());
	        e.printStackTrace();
	        throw new RuntimeException("예약 변경 실패: " + e.getMessage(), e);
	    }
	}

	// 실증 상품 등록 페이지에서 실증 상품 등록하는 기능
	@Override
	public void addDemonstration(DemonstrationFormReqDTO demonstrationFormDTO, List<MultipartFile> imageList,
			String memId) {
		Demonstration demonstration = Demonstration.builder().demName(demonstrationFormDTO.getDemName())
				.demInfo(demonstrationFormDTO.getDemInfo()).demMfr(demonstrationFormDTO.getDemMfr())
				.itemNum(demonstrationFormDTO.getItemNum()).category(demonstrationFormDTO.getCategory()).build();

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

	// 상품 수정
	@Override
	@Transactional
	public void updateDemonstration(DemonstrationFormReqDTO demonstrationFormDTO, List<MultipartFile> imageList,
			String memId) {
		// 실증 상품 정보 업데이트
		demonstrationRepository.updateDem(demonstrationFormDTO.getDemName(), demonstrationFormDTO.getDemMfr(),
				demonstrationFormDTO.getItemNum(), demonstrationFormDTO.getDemInfo(), demonstrationFormDTO.getDemNum(),
				demonstrationFormDTO.getCategory());

		// 반납 예정일 수정
		demonstrationRegistrationRepository.updateDemRegChangeExpDate(demonstrationFormDTO.getExpDate(),
				demonstrationFormDTO.getDemNum(), memId);

		// 기존 상품 이미지 불러옴(폴더에서 이미지 삭제 위해)
		List<DemonstrationImageDTO> deleteImageList = demonstrationImageRepository
				.selectDemImageIn(List.of(demonstrationFormDTO.getDemNum()));
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
		demonstrationImageRepository.deleteDemNumImage(List.of(demonstrationFormDTO.getDemNum()));

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
	public void deleteDemonstration(List<Long> demNum) {
		demonstrationRegistrationRepository.updateDemRegChangeState(DemonstrationState.CANCEL, demNum);
		List<String> rentalMemId = new ArrayList<>();
		rentalMemId = demonstrationReserveRepository.getResMemId(demNum, DemonstrationState.CANCEL);
		//demonstrationReservationCancels(demNum, rentalMemId);
	}

	// 실증 번호를 받아서 실증 상품의 정보를 받아오는 기능
	@Override
	public DemonstrationFormResDTO selectOne(Long demNum) {
		Demonstration entity = demonstrationRepository.findById(demNum)
				.orElseThrow(() -> new RuntimeException("해당 번호의 실증 정보가 없습니다: " + demNum));
		DemonstrationFormResDTO dto = modelMapper.map(entity, DemonstrationFormResDTO.class);
		List<DemonstrationImageDTO> imageDtoList = demonstrationImageRepository.selectDemImageIn(List.of(demNum));

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
		List<DemonstrationState> state = new ArrayList<>();
		state.add(DemonstrationState.WAIT);
		state.add(DemonstrationState.EXPIRED);
		state.add(DemonstrationState.ACCEPT);
		Boolean bool = demonstrationReserveRepository.checkRes(demNum, memId, state).orElse(false); // 이력에 wait,
																									// expired, accept가
																									// 하나라도 잇다면, 예약 불가능
		return bool;
	}

	// 물품 대여 리스트 페이지에서 연기 신청, 반납 신청 하는 기능
	@Override
	public void addRequest(ResRequestDTO resRequestDTO, String memId) {
		System.out.println(resRequestDTO);
		List<DemonstrationState> states = Arrays.asList(DemonstrationState.ACCEPT);
		DemonstrationReserve reserve=demonstrationReserveRepository.findDemNumMemId(Arrays.asList(resRequestDTO.getDemNum()),memId,states);
		if(resRequestDTO.getType().equals(RequestType.EXTEND))
		{
			List<DemonstrationTime> demonstrationTimeList = new ArrayList<>();
			for (LocalDate date = reserve.getStartDate(); !date
					.isAfter(resRequestDTO.getUpdateDate()); date = date.plusDays(1)) {
				DemonstrationTime demonstrationTime = DemonstrationTime.builder().demDate(date)
						.demonstration(Demonstration.builder().demNum(resRequestDTO.getDemNum()).build())
						.build();
				demonstrationTimeList.add(demonstrationTime);
			} // 변경 전 날짜로 부터 변경 후 까지의 날짜의 예약 상태 추가
				// time 리스트를 저장
			demonstrationTimeRepository.saveAll(demonstrationTimeList);
		}
		DemonstrationRequest request = new DemonstrationRequest();
		request.setApplyAt(LocalDate.now());
		request.setReserve(reserve);
		request.setUpdateDate(resRequestDTO.getUpdateDate());
		request.setState(DemonstrationState.WAIT);
		request.setType(resRequestDTO.getType());
		demonstrationRequestRepository.save(request);
	}

	// 실증 등록한 기업의 물품 리스트를 보여주는 기능
	@Override
	public PageResponseDTO<DemonstrationBorrowListDTO> AllgetBorrow(String memId, DemonstrationSearchDTO searchDTO) {
		// 페이징 & 정렬
		int pageCount = searchDTO.getPageCount() != null && searchDTO.getPageCount() >= 0 ? searchDTO.getPageCount()
				: 0;
		Sort.Direction direction = "desc".equalsIgnoreCase(searchDTO.getSort()) ? Sort.Direction.DESC
				: Sort.Direction.ASC;
		Sort sortObj = "expDate".equalsIgnoreCase(searchDTO.getSortBy()) ? Sort.by(direction, "expDate")
				: Sort.by(direction, "regDate");
		Pageable pageable = PageRequest.of(pageCount, 10, sortObj);

		// Specification 생성
		Specification<DemonstrationRegistration> spec = DemonstrationRegistrationSpecs.withSearchAndSort(memId,
				searchDTO.getType(), searchDTO.getSearch(), searchDTO.getSortBy(), searchDTO.getSort(),
				searchDTO.getStatusFilter());

		Page<DemonstrationRegistration> entityPage = demonstrationRegistrationRepository.findAll(spec, pageable);

		// demNum -> 이미지 리스트 매핑
		Map<Long, List<DemonstrationImageDTO>> demNumToImages = demonstrationImageRepository
				.selectDemImageIn(entityPage.stream().map(r -> r.getDemonstration().getDemNum()).distinct().toList())
				.stream().collect(Collectors.groupingBy(DemonstrationImageDTO::getDemNum));

		// DTO 변환
		List<DemonstrationBorrowListDTO> dtoList = entityPage.stream().map(reg -> {
			Demonstration dem = reg.getDemonstration();
			DemonstrationBorrowListDTO dto = new DemonstrationBorrowListDTO(dem.getDemNum(), dem.getDemName(),
					dem.getItemNum(), dem.getDemMfr(), reg.getExpDate(), reg.getRegDate(), reg.getState());
			dto.setImageList(demNumToImages.getOrDefault(dem.getDemNum(), List.of()));
			return dto;
		}).toList();

		return new PageResponseDTO<>(dtoList, entityPage.getTotalPages(), entityPage.getNumber(),
				entityPage.getTotalElements());
	}

}
