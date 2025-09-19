package com.EduTech.service.demonstration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.EduTech.entity.member.Member;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRequestRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
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

	// 교사가 빌린 물품 내역 페이지 조회
	@Override
	public PageResponseDTO<DemonstrationListReserveDTO> getAllDemResRental(DemonstrationSearchDTO searchDTO,
			String memId) {
		DemonstrationState statusEnum = null;
		if (searchDTO.getStatusFilter() != null && !searchDTO.getStatusFilter().isEmpty()) {
			try {
				statusEnum = DemonstrationState.valueOf(searchDTO.getStatusFilter());
			} catch (IllegalArgumentException e) {
				// 잘못된 enum 값이면 null로 처리
				statusEnum = null;
			}
		}

		int pageCount = searchDTO.getPageCount() != null && searchDTO.getPageCount() >= 0 ? searchDTO.getPageCount()
				: 0;
		String sortBy = searchDTO.getSortBy() != null && !searchDTO.getSortBy().isEmpty() ? searchDTO.getSortBy()
				: "applyAt";
		String sort = searchDTO.getSort() != null && !searchDTO.getSort().isEmpty() ? searchDTO.getSort() : "desc";

		Pageable pageable = PageRequest.of(pageCount, 10);

		// 하나의 레포지토리 메서드로 모든 조건 처리
		Page<DemonstrationListReserveDTO> resPage = demonstrationReserveRepository.getRentalToDemNum(
				searchDTO.getType(), searchDTO.getSearch(), statusEnum, searchDTO.getDemNum(), sortBy, sort, pageable);

		return new PageResponseDTO<>(resPage);

	}

	// 실증 물품 대여 조회
	@Override
	public PageResponseDTO<DemonstrationRentalListDTO> getAllDemRental(String memId, DemonstrationSearchDTO searchDTO) {
	    DemonstrationState statusEnum = null;
	    if (searchDTO.getStatusFilter() != null && !searchDTO.getStatusFilter().isEmpty()) {
	        try {
	            statusEnum = DemonstrationState.valueOf(searchDTO.getStatusFilter());
	        } catch (IllegalArgumentException e) {
	            // 잘못된 enum 값이면 null로 처리
	            statusEnum = null;
	        }
	    }

	    // 페이징 & 정렬 기본값 설정
	    int pageCount = searchDTO.getPageCount() != null && searchDTO.getPageCount() >= 0 ? searchDTO.getPageCount() : 0;
	    String sortBy = searchDTO.getSortBy() != null && !searchDTO.getSortBy().isEmpty() ? searchDTO.getSortBy() : "applyAt";
	    String sort = searchDTO.getSort() != null && !searchDTO.getSort().isEmpty() ? searchDTO.getSort() : "desc";

	    Pageable pageable = PageRequest.of(pageCount, 10);

	    // 쿼리에서 바로 DTO로 매핑하여 가져오기
	    Page<DemonstrationRentalListDTO> resPage = demonstrationReserveRepository.getRentalToMemId(memId,
	            searchDTO.getType(), searchDTO.getSearch(), statusEnum, searchDTO.getDemNum(), sortBy, sort, pageable);

	    // resPage에서 demRevNum 리스트 추출
	    List<Long> demRevNums = resPage.getContent().stream()
	            .map(DemonstrationRentalListDTO::getDemRevNum)
	            .distinct()
	            .toList();

	    // demRevNum으로 Request 데이터 조회 및 그룹화
	    Map<Long, List<DemonstrationRequest>> demRevNumToRequests = demonstrationRequestRepository
	            .findStateByDemRevNumIn(demRevNums).stream()
	            .collect(Collectors.groupingBy(r -> r.getReserve().getDemRevNum()));

	    // 각 DTO에 request 정보 및 이미지 정보 설정
	    List<DemonstrationRentalListDTO> updatedContent = resPage.getContent().stream()
	            .map(dto -> {
	                // Request 정보 설정
	                List<DemonstrationRequest> reqList = demRevNumToRequests.getOrDefault(dto.getDemRevNum(), List.of());
	                dto.setRequestType(reqList.isEmpty() ? null : reqList.stream().map(DemonstrationRequest::getType).toList());
	                dto.setReqState(reqList.isEmpty() ? null : reqList.stream().map(DemonstrationRequest::getState).toList());
	                
	                // 메인 이미지 설정
	                Long demNum = dto.getDemNum();
	                if (demNum != null) {
	                    try {
	                        DemonstrationImageDTO mainImage = demonstrationImageRepository.selectDemImageMain(demNum);
	                        dto.setMainImage(mainImage);
	                    } catch (Exception e) {
	                        dto.setMainImage(null);
	                    }
	                } else {
	                    dto.setMainImage(null);
	                }
	                
	                return dto;
	            })
	            .toList();

	    // 기존 페이지 정보로 새로운 Page 객체 생성
	    Page<DemonstrationRentalListDTO> finalPage = new PageImpl<>(updatedContent, pageable, resPage.getTotalElements());

	    return new PageResponseDTO<>(finalPage);
	}

	// 해당 상품의 예약 정보를 가져오는 기능(실증 장비 신청 페이지에서 대여가능 / 예약 마감 표기 할거임)
	@Override
	public List<LocalDate> checkReservationState(DemonstrationTimeReqDTO demonstrationTimeReqDTO) {
		// 시작 날짜와 끝 날짜, 실증 번호를 통해 예약 날짜 리스트를 불러옴(wait와 accept 상태의 경우에만 time테이블에 저장되므로
		// 상태값 안넣어도 됨)
		List<DemonstrationTimeResDTO> dateList = demonstrationTimeRepository.findReservedDates(
				demonstrationTimeReqDTO.getStartDate(), demonstrationTimeReqDTO.getEndDate(),
				demonstrationTimeReqDTO.getDemNum());
		// DTO 리스트에서 LocalDate만 추출
		return dateList.stream().map(DemonstrationTimeResDTO::getDemDate).collect(Collectors.toList()); 
	}

	// 현재 회원의 예약 정보를 제외한 상품 예약 정보 가져오기
	@Override
	public List<LocalDate> checkReservationStateExcept(DemonstrationTimeReqDTO dto, String memId) {
		// 조회할 상태 (대기, 수락)
		List<DemonstrationState> states = Arrays.asList(DemonstrationState.ACCEPT, DemonstrationState.WAIT);

		// 1. 현재 달 범위 내 모든 예약 날짜 가져오기 (모든 회원)
		List<DemonstrationTimeResDTO> reservedDates = demonstrationTimeRepository.findReservedDates(dto.getStartDate(),
				dto.getEndDate(), dto.getDemNum());
		
		// 2. 로그인한 회원의 예약 정보 가져오기
		List<DemonstrationTimeReqDTO> memberReservations = demonstrationReserveRepository.getResDate(dto.getDemNum(),
				memId, states);
		
		// 로그인 회원 예약 날짜만 추출
		List<LocalDate> memberReservedDates = memberReservations.stream()
				.flatMap(r -> r.getStartDate().datesUntil(r.getEndDate().plusDays(1))).collect(Collectors.toList());

		// 3. 현재 달 예약 날짜에서 로그인 회원 예약 제외
		List<LocalDate> availableDates = reservedDates.stream().map(DemonstrationTimeResDTO::getDemDate)
				.filter(d -> !memberReservedDates.contains(d)).collect(Collectors.toList());

		return availableDates;
	}

	// 실증 장비신청 페이지 (실증 물품 리스트 목록)
	@Override
	public PageResponseDTO<DemonstrationPageListDTO> getAllDemList(DemonstrationSearchDTO searchDTO) {
		int pageCount = searchDTO.getPageCount() != null && searchDTO.getPageCount() >= 0
	            ? searchDTO.getPageCount()
	            : 0;
	    String sortBy = searchDTO.getSortBy() != null && !searchDTO.getSortBy().isEmpty()
	            ? searchDTO.getSortBy()
	            : "desc";
	    String sort = searchDTO.getSort() != null && !searchDTO.getSort().isEmpty()
	            ? searchDTO.getSort()
	            : "expDate";

	    Pageable pageable = PageRequest.of(pageCount, 4);

	    // 쿼리에서 바로 DTO로 매핑
	    Page<DemonstrationPageListDTO> demPage = demonstrationRepository.getDemList(
	            searchDTO.getType(), searchDTO.getSearch(), sortBy, sort,DemonstrationState.ACCEPT, pageable);

	    // 리스트 가져오기
	    List<DemonstrationPageListDTO> list = demPage.getContent();

	    // 한번에 이미지 조회
	    List<Long> demNumList = list.stream()
	            .map(DemonstrationPageListDTO::getDemNum)
	            .toList();

	    List<DemonstrationImageDTO> allImages = demonstrationImageRepository.selectDemImageIn(demNumList);

	    // 이미지 매핑
	    List<DemonstrationPageListDTO> updatedContent = list.stream()
	            .map(dto -> {
	                dto.setImageList(allImages.stream()
	                        .filter(img -> img.getDemNum().equals(dto.getDemNum()))
	                        .toList());
	                return dto;
	            })
	            .toList();

	    // 새 Page 객체 생성
	    Page<DemonstrationPageListDTO> finalPage =
	            new PageImpl<>(updatedContent, pageable, demPage.getTotalElements());

	    return new PageResponseDTO<>(finalPage);
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
		
		DemonstrationTimeReqDTO demonstrationTimeReqDTO = new DemonstrationTimeReqDTO();
		demonstrationTimeReqDTO.setDemNum(demonstrationReservationDTO.getDemNum());
		demonstrationTimeReqDTO.setStartDate(demonstrationReservationDTO.getStartDate());
		demonstrationTimeReqDTO.setEndDate(demonstrationReservationDTO.getEndDate());
		Long beforeItemNum = demonstrationRepository.selectItemNum(demonstrationReservationDTO.getDemNum());
		List<LocalDate> ResState = checkReservationState(demonstrationTimeReqDTO);
		Member member = memberRepository.findById(memId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다"));

		// 전달한 예약일에 예약이 없다면,
		if (ResState == null || ResState.isEmpty()) {
			if(beforeItemNum<demonstrationReservationDTO.getItemNum())
			{
				throw new RuntimeException("신청한 수량이 현재 재고량 보다 많습니다!");
			}
			int result = demonstrationRepository.updateItemNum(beforeItemNum-demonstrationReservationDTO.getItemNum(),
					demonstrationReservationDTO.getDemNum()); // 상품을 현재 재고량으로 업데이트
			DemonstrationReserve demonstrationReserve = DemonstrationReserve.builder().applyAt(LocalDate.now())
					.startDate(demonstrationReservationDTO.getStartDate())
					.endDate(demonstrationReservationDTO.getEndDate()).state(DemonstrationState.WAIT)
					.demonstration(Demonstration.builder().demNum(demonstrationReservationDTO.getDemNum()).build())
					.member(member).bItemNum(demonstrationReservationDTO.getItemNum()).build(); 
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
			throw new RuntimeException("예약된 날짜가 겹쳐 있습니다!");
		}
	}

	
	// 물품대여 페이지에서 예약 취소 버튼 클릭 시, 상태값을 cancel로 바꿈 관리자도 포함
	// 예약 변경시에도 호출
	@Override
	@Transactional
	public void demonstrationReservationCancel(List<Long> demRevNum) {
		List<DemonstrationState> state = new ArrayList<>();
		state.add(DemonstrationState.ACCEPT);
		state.add(DemonstrationState.WAIT);
		state.add(DemonstrationState.EXPIRED);
		List<DemonstrationReserve> demonstrationReserve = demonstrationReserveRepository.findDemRevNum(demRevNum,
				state);
		if (demonstrationReserve == null) {
			throw new RuntimeException("예약된 정보가 없습니다.");
		}

		for (DemonstrationReserve res : demonstrationReserve) {
			String memId = res.getMember().getMemId();
			Long demNum = res.getDemonstration().getDemNum();

			// 현재 itemNum 가져오기
			Long currentItemNum = demonstrationRepository.selectItemNum(demNum);

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
			demonstrationTimeRepository.deleteTimeDemNum(deleteTimeList, res.getDemonstration().getDemNum());
		}
	}

	// 실증 대여 페이지에서 예약 변경하기 클릭 시, 예약 정보 변경
	@Override
	@Transactional
	public void demonstrationReservationChange(DemonstrationReservationDTO demonstrationReservationDTO, String memId)
	{
		try {
	        List<Long> cancelList = Arrays.asList(demonstrationReservationDTO.getDemRevNum());
	        demonstrationReservationCancel(cancelList);
	        DemonstrationReservationDTO newReservationDTO = new DemonstrationReservationDTO();
	        newReservationDTO.setDemNum(demonstrationReservationDTO.getDemNum());
	        newReservationDTO.setStartDate(demonstrationReservationDTO.getStartDate());
	        newReservationDTO.setEndDate(demonstrationReservationDTO.getEndDate());
	        newReservationDTO.setItemNum(demonstrationReservationDTO.getItemNum()); // 사용자가 신청한 재고량
	        
	        demonstrationReservation(newReservationDTO, memId);

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("예약 변경 실패: " + e.getMessage(), e);
	    }
	}

	// 실증 상품 등록 페이지에서 실증 상품 등록하는 기능
	@Override
	public void addDemonstration(DemonstrationFormReqDTO demonstrationFormDTO, List<MultipartFile> imageList,
			String memId) {
		
		checkImageList(imageList); // 유효성 검사
		
		Demonstration demonstration = Demonstration.builder().demName(demonstrationFormDTO.getDemName())
				.demInfo(demonstrationFormDTO.getDemInfo()).demMfr(demonstrationFormDTO.getDemMfr())
				.itemNum(demonstrationFormDTO.getItemNum()).category(demonstrationFormDTO.getCategory()).build();
		
		// 실증 물품 등록
		demonstrationRepository.save(demonstration);
		Long demNum = demonstration.getDemNum();

		Member member = memberRepository.findById(memId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다"));
		DemonstrationRegistration demonstrationRegistration = DemonstrationRegistration.builder()
				.regDate(LocalDate.now()).expDate(demonstrationFormDTO.getExpDate()).state(DemonstrationState.WAIT)
				.demonstration(Demonstration.builder().demNum(demNum).build()).member(member).build();

		// 실증 등록
		demonstrationRegistrationRepository.save(demonstrationRegistration);

		// 폴더에 이미지 저장 (demImages라는 폴더에)
		List<Object> files = fileUtil.saveFiles(imageList, "demImages");

		Integer mainIndex = demonstrationFormDTO.getMainImageIndex(); 
		if(mainIndex==null)
		{
			mainIndex=0;
		}

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
		
		checkImageList(imageList); // 유효성 검사
		
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
			filePaths.add(path);
		}

		// 폴더에서 이미지 삭제
		fileUtil.deleteFiles(filePaths);

		// 기존 상품 이미지 삭제 후,
		demonstrationImageRepository.deleteDemNumImage(List.of(demonstrationFormDTO.getDemNum()));
		
		if (imageList != null && !imageList.isEmpty()) {
			List<Object> files = fileUtil.saveFiles(imageList, "demImages");

			Integer mainIndex = demonstrationFormDTO.getMainImageIndex(); 
			if(mainIndex==null)
			{
				mainIndex=0;
			}
			for (int i = 0; i < files.size(); i++) {
				Object obj = files.get(i);
				if (obj instanceof Map) {
					Map<String, String> map = (Map<String, String>) obj;
					DemonstrationImage demonstrationimage = DemonstrationImage.builder()
							.imageName(map.get("originalName")).imageUrl(map.get("filePath")).isMain(i == mainIndex) 																												
							.demonstration(Demonstration.builder().demNum(demonstrationFormDTO.getDemNum()).build())
							.build();
					demonstrationImageRepository.save(demonstrationimage);
				}
			}
		}
	}

	// 실증 번호를 받아서 실증 상품을 삭제하는 기능
	@Transactional
	@Override
	public void deleteDemonstration(List<Long> demNum) {
	    if (demNum == null || demNum.isEmpty()) {
	        throw new RuntimeException("삭제할 실증 번호가 없습니다.");
	    }
	    try {
	        demonstrationRegistrationRepository.updateDemRegChangeState(DemonstrationState.CANCEL, demNum);
	        demonstrationReserveRepository.updateDemResChangeStateToDemNum(DemonstrationState.CANCEL, demNum);
	    } catch (Exception e) {
	        throw new RuntimeException("실증 상품 삭제 중 오류가 발생했습니다.");
	    }
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
		Boolean bool = demonstrationReserveRepository.checkRes(demNum, memId, state).orElse(false);
																							
		return bool;
	}

	// 물품 대여 리스트 페이지에서 연기 신청, 반납 신청 하는 기능
	@Override
	public void addRequest(ResRequestDTO resRequestDTO, String memId) {
		List<DemonstrationState> states = Arrays.asList(DemonstrationState.ACCEPT);
		DemonstrationReserve reserve = demonstrationReserveRepository
				.findDemNumMemId(Arrays.asList(resRequestDTO.getDemNum()), memId, states);
		if (resRequestDTO.getType().equals(RequestType.EXTEND)) {
			List<DemonstrationTime> demonstrationTimeList = new ArrayList<>();
			for (LocalDate date = reserve.getStartDate(); !date.isAfter(resRequestDTO.getUpdateDate()); date = date
					.plusDays(1)) {
				DemonstrationTime demonstrationTime = DemonstrationTime.builder().demDate(date)
						.demonstration(Demonstration.builder().demNum(resRequestDTO.getDemNum()).build()).build();
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
	    DemonstrationState statusEnum = null;
	    if (searchDTO.getStatusFilter() != null && !searchDTO.getStatusFilter().isEmpty()) {
	        try {
	            statusEnum = DemonstrationState.valueOf(searchDTO.getStatusFilter());
	        } catch (IllegalArgumentException e) {
	            // 잘못된 enum 값이면 null로 처리
	            statusEnum = null;
	        }
	    }

	    // 페이징 & 정렬 기본값 설정
	    int pageCount = searchDTO.getPageCount() != null && searchDTO.getPageCount() >= 0 ? searchDTO.getPageCount() : 0;
	    String sortBy = searchDTO.getSortBy() != null && !searchDTO.getSortBy().isEmpty() ? searchDTO.getSortBy() : "regDate";
	    String sort = searchDTO.getSort() != null && !searchDTO.getSort().isEmpty() ? searchDTO.getSort() : "desc";

	    Pageable pageable = PageRequest.of(pageCount, 10);

	    // 레포지토리에서 바로 DTO로 조회
	    Page<DemonstrationBorrowListDTO> borrowPage = demonstrationRegistrationRepository.getBorrowListByMemId(
	            memId, searchDTO.getType(), searchDTO.getSearch(), statusEnum, sortBy, sort, pageable);

	    // 각 DTO에 메인 이미지 세팅
	    List<DemonstrationBorrowListDTO> updatedContent = borrowPage.getContent().stream()
	            .map(dto -> {
	                Long demNum = dto.getDemNum();
	                if (demNum != null) {
	                    try {
	                        DemonstrationImageDTO mainImage = demonstrationImageRepository.selectDemImageMain(demNum);
	                        dto.setMainImage(mainImage);
	                    } catch (Exception e) {
	                        dto.setMainImage(null);
	                    }
	                } else {
	                    dto.setMainImage(null);
	                }
	                return dto;
	            })
	            .collect(Collectors.toList());

	    // 새로운 Page 객체 생성
	    Page<DemonstrationBorrowListDTO> finalPage =
	            new PageImpl<>(updatedContent, pageable, borrowPage.getTotalElements());

	    // PageResponseDTO로 변환
	    return new PageResponseDTO<>(finalPage);
	}

	
	
	// 물품 등록 및 수정 이미지 리스트 유효성 검사
	public void checkImageList(List<MultipartFile> imageList) {
		final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
	    final List<String> ALLOWED_EXT = Arrays.asList("jpg", "jpeg", "png");

	    if (imageList == null || imageList.isEmpty()) {
	        throw new RuntimeException("이미지는 최소 1개 이상 업로드해야 합니다.");
	    }

	    if (imageList.size() > 8) {
	        throw new RuntimeException("이미지는 최대 8개까지 업로드 가능합니다.");
	    }

	    for (MultipartFile file : imageList) {
	        String originalName = file.getOriginalFilename();
	        if (originalName == null) {
	            throw new RuntimeException("파일 이름이 없습니다.");
	        }

	        // 확장자 검사
	        String ext = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
	        if (!ALLOWED_EXT.contains(ext)) {
	            throw new RuntimeException("jpg, jpeg, png 파일만 업로드 가능합니다. (" + originalName + ")");
	        }

	        // 용량 검사
	        if (file.getSize() > MAX_FILE_SIZE) {
	            throw new RuntimeException("100MB 이하 파일만 업로드 가능합니다. (" + originalName + ")");
	        }
	    }
		
	}
}
