package com.EduTech.service.demonstration;

import java.time.LocalDate;
import java.util.ArrayList;
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

	// 실증 물품 등록 확인 페이지에서 해당 물품을 대여 신청한 사람들을 불러오는 기능
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
				dto.setPhone(m.getPhone());
				if (m.getTeacher() != null)
					dto.setSchoolName(m.getTeacher().getSchoolName());
			}
			return dto;
		}).toList();

		// List와 페이징 정보로 PageResponseDTO 생성
		return new PageResponseDTO<>(dtoList, resPage.getTotalPages(), resPage.getNumber(), resPage.getTotalElements());
	}

	// 선생이 빌린 물품 내역 페이지 조회
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

	// 물품 대여 조회 페이지 연기 신청 및 반납 조기 신청
	@Override
	public void rentalDateChange(DemonstrationResRentalDTO demonstrationResRentalDTO) {
		// demonstrationRes테이블 endDate 변경
		demonstrationReserveRepository.updateDemResEndDate(demonstrationResRentalDTO.getDemRevNum(),
				demonstrationResRentalDTO.getUpdatedEndDate(),
				DemonstrationState.CANCEL);

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
	    if (pageCount == null || pageCount < 0) pageCount = 0;

	    Page<DemonstrationPageListDTO> currentPage;

	    if ("demName".equals(type) && !search.isEmpty()) {
	        currentPage = demonstrationRepository.selectPageDemName(
	                PageRequest.of(pageCount, 4, Sort.by("demNum").descending()), search, DemonstrationState.ACCEPT);
	    } else if ("demMfr".equals(type) && !search.isEmpty()) {
	        currentPage = demonstrationRepository.selectPageDemMfr(
	                PageRequest.of(pageCount, 4, Sort.by("demNum").descending()), search, DemonstrationState.ACCEPT);
	    } else {
	        currentPage = demonstrationRepository.selectPageDem(
	                PageRequest.of(pageCount, 4, Sort.by("demNum").descending()), DemonstrationState.ACCEPT);
	    }

	    // list의 값을 넣지만 currentPage에도 setImage가 적용된다.
	    // 얕은 참조 복사이기 때문에
	    List<DemonstrationPageListDTO> list = currentPage.getContent();

	    // 반복문으로 리포지토리를 여러번 호출하는 문제 해결위해 만든 demNumList
	    List<Long> demNumList = list.stream().map(DemonstrationPageListDTO::getDemNum).toList();

	    // 같은 demNum을 갖는 이미지가 하나의 객체에 다 들어있음
	    List<DemonstrationImageDTO> allImages = demonstrationImageRepository.selectDemImageIn(demNumList);

	    // 각 DTO에 이미지 리스트 set
	    list.forEach(dto -> dto.setImageList(
	            allImages.stream()
	                     .filter(img -> img.getDemNum().equals(dto.getDemNum()))
	                     .toList()
	    ));

	    return new PageResponseDTO<>(list, currentPage.getTotalPages(), currentPage.getNumber(), currentPage.getTotalElements());
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
		List<DemonstrationReserve> demonstrationReserves = demonstrationReserveRepository.findDemRevNums(memIds,
				demNums, DemonstrationState.CANCEL);

		if (demonstrationReserves.isEmpty()) {
			System.out.println("예약 정보가 없습니다.");
			return;
		}

		// 각 demNum별로 취소된 갯수 + 기존 갯수를 업데이트 (batch 처리 메서드로 교체 권장)
		for (Long demNum : demNums) {
			Long updateItemNum = demonstrationReserveRepository.getBItemNumBatch(demNum, memIds,
					DemonstrationState.CANCEL);
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
	public void deleteDemonstration(Long demNum, String memId) {
		demonstrationRegistrationRepository.updateDemRegChangeState(DemonstrationState.CANCEL, memId, demNum);
		List<Long> demNums = new ArrayList<>();
		demNums.add(demNum);
		List<String> rentalMemId = new ArrayList<>();
		rentalMemId = demonstrationReserveRepository.getResMemId(demNum, DemonstrationState.CANCEL);
		demonstrationReservationCancels(demNums, rentalMemId);
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
