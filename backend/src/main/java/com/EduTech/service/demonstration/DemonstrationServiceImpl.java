package com.EduTech.service.demonstration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.demonstration.DemonstrationDetailDTO;
import com.EduTech.dto.demonstration.DemonstrationFormDTO;
import com.EduTech.dto.demonstration.DemonstrationImageDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationPageListDTO;
import com.EduTech.dto.demonstration.DemonstrationRentalListDTO;
import com.EduTech.dto.demonstration.DemonstrationResRentalDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationCancelDTO;
import com.EduTech.dto.demonstration.DemonstrationReservationDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeReqDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeResDTO;
import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationImage;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.DemonstrationTime;
import com.EduTech.entity.member.Member;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
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
	MemberRepository memberRepository;
	@Autowired
	ModelMapper modelMapper;

	// 실증 교사 신청목록 조회 기능 (검색도 같이 구현할 것임.)
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

	// 실증 기업 신청목록 조회 기능 (검색도 같이 구현할 것임.)
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

	// 회원이 신청한 물품 대여 조회 페이지 조회 기능 (검색도 같이 구현할 것임.)
	@Override
	public PageResponseDTO<DemonstrationRentalListDTO> getAllDemRental(String memId, String search, Integer pageCount) {

		if (pageCount == null || pageCount < 0) {
			pageCount = 0;
		}

		if (!StringUtils.hasText(search)) { // 검색어 입력이 없을 경우,
			Page<DemonstrationRentalListDTO> currentPage = demonstrationRepository
					.selectPageViewDem(PageRequest.of(pageCount, 10, Sort.by("demNum").descending()), memId);

			return new PageResponseDTO<DemonstrationRentalListDTO>(currentPage); // 페이지 DTO 객체 리턴
		} else { // 검색어 입력을 했을 경우,
			Page<DemonstrationRentalListDTO> currentPage = demonstrationRepository.selectPageViewDemSearch(
					PageRequest.of(pageCount, 10, Sort.by("demNum").descending()), memId, search);

			return new PageResponseDTO<DemonstrationRentalListDTO>(currentPage); // 페이지 DTO 객체 리턴
		}

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
			dates.add(DemonstrationTime.builder().demDate(date).state(true)
					.demonstration(Demonstration.builder().demNum(demonstrationResRentalDTO.getDemNum()).build())
					.build());
		}

		demonstrationTimeRepository.saveAll(dates);
	}

	// 실증 장비신청 페이지 (실증 물품 리스트 목록) - 이미지도 가져와야함
	@Override
	public PageResponseDTO<DemonstrationPageListDTO> getAllDemList(Integer pageCount) {
		if (pageCount == null || pageCount < 0) {
			pageCount = 0;
		}

		Page<DemonstrationPageListDTO> currentPage = demonstrationRepository
				.selectPageDem(PageRequest.of(pageCount, 4, Sort.by("demNum").descending()));

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

	// 해당 상품이 예약 상태인지 확인 가능 (실증 장비 신청 페이지에서 대여가능 / 예약 마감 표기 할거임)
	@Override
	public List<DemonstrationTimeResDTO> checkReservationState(DemonstrationTimeReqDTO demonstrationTimeReqDTO) {
		// 시작 날짜와 끝 날짜, 실증 번호를 통해 예약 날짜 리스트를 불러와 리턴
		List<DemonstrationTimeResDTO> DateList = demonstrationTimeRepository.findReservedDates(
				demonstrationTimeReqDTO.getStartDate(), demonstrationTimeReqDTO.getEndDate(),
				demonstrationTimeReqDTO.getDemNum());
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
	public void demonstrationReservation(DemonstrationReservationDTO demonstrationReservationDTO) {
		// 선택한 실증 상품의 예약된 상태를 불러오기 위해 사용한 dto
		DemonstrationTimeReqDTO demonstrationTimeReqDTO = new DemonstrationTimeReqDTO();
		demonstrationTimeReqDTO.setDemNum(demonstrationReservationDTO.getDemNum());
		demonstrationTimeReqDTO.setStartDate(demonstrationReservationDTO.getStartDate());
		demonstrationTimeReqDTO.setEndDate(demonstrationReservationDTO.getEndDate());
		List<DemonstrationTimeResDTO> ResState = checkReservationState(demonstrationTimeReqDTO);

		if (ResState == null || ResState.isEmpty()) {
			DemonstrationReserve demonstrationReserve = DemonstrationReserve.builder().applyAt(LocalDate.now())
					.startDate(demonstrationReservationDTO.getStartDate())
					.endDate(demonstrationReservationDTO.getEndDate()).state(DemonstrationState.WAIT)
					.demonstration(Demonstration.builder().demNum(demonstrationReservationDTO.getDemNum()).build())
					.member(Member.builder().memId(demonstrationReservationDTO.getMemId()).build()).build();
			demonstrationReserveRepository.save(demonstrationReserve);

			// 실증 신청 시 예약된 날짜도 추가되야 하므로
			// demTime 테이블에 예약된 시간 추가
			List<DemonstrationTime> demonstrationTimeList = new ArrayList<>();
			for (LocalDate date = demonstrationReservationDTO.getStartDate(); !date
					.isAfter(demonstrationReservationDTO.getEndDate()); date = date.plusDays(1)) {
				DemonstrationTime demonstrationTime = DemonstrationTime.builder().demDate(date).state(true)
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

	// 실증 신청 상세 페이지에서 예약 취소하기 클릭 시, 예약 정보 취소
	@Override
	public void demonstrationReservationCancel(DemonstrationReservationCancelDTO demonstrationReservationCancelDTO) {
		// 불러온 아이디와 실증 번호를 통해 신청 번호를 받아온 후,
		DemonstrationReserve demonstrationReserve = demonstrationReserveRepository.findDemRevNum(
				demonstrationReservationCancelDTO.getMemId(), demonstrationReservationCancelDTO.getDemNum());

		if (demonstrationReserve == null) {
			System.out.println("예약 정보가 없습니다.");
			return;
		}

		// 신청 번호를 통해 삭제
		demonstrationReserveRepository.deleteOneDemRes(demonstrationReserve.getDemRevNum());
		// demonstartionTime테이블에 있는 예약 정보도 삭제
		List<LocalDate> deleteTimeList = new ArrayList<>();
		for (LocalDate date = demonstrationReserve.getStartDate(); !date
				.isAfter(demonstrationReserve.getEndDate()); date = date.plusDays(1)) {
			deleteTimeList.add(date);
		}
		// 저장되어 있던 시작 번호와 끝 번호를 가져와
		// time테이블의 예약 정보도 삭제
		demonstrationTimeRepository.deleteDemTimeList(deleteTimeList);
	}

	// 실증 신청 상세 페이지에서 예약 변경하기 클릭 시, 예약 정보 변경
	@Override
	public void demonstrationReservationChange(DemonstrationReservationDTO demonstrationReservationDTO) {
		// 기존 예약 취소
		DemonstrationReservationCancelDTO demonstrationReservationcancelDTO = new DemonstrationReservationCancelDTO();
		demonstrationReservationcancelDTO.setMemId(demonstrationReservationDTO.getMemId());
		demonstrationReservationcancelDTO.setDemNum(demonstrationReservationDTO.getDemNum());
		demonstrationReservationCancel(demonstrationReservationcancelDTO);
		// 새로운 예약 추가
		demonstrationReservation(demonstrationReservationDTO);
	}

	// 실증 상품 등록 페이지에서 실증 상품 등록하는 기능
	@Override
	public void addDemonstration(DemonstrationFormDTO demonstrationFormDTO) {
		Demonstration demonstration = Demonstration.builder().demName(demonstrationFormDTO.getDemName())
				.demInfo(demonstrationFormDTO.getDemInfo()).demMfr(demonstrationFormDTO.getDemMfr())
				.itemNum(demonstrationFormDTO.getItemNum()).build();

		// 실증 물품 등록
		demonstrationRepository.save(demonstration);
		Long demNum = demonstration.getDemNum();

		//System.out.println(memId);
		Member member = memberRepository.findById("tee1694").orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다"));
		DemonstrationRegistration demonstrationRegistration = DemonstrationRegistration.builder()
				.regDate(LocalDate.now()).expDate(demonstrationFormDTO.getExpDate()).state(DemonstrationState.WAIT)
				.demonstration(Demonstration.builder().demNum(demNum).build()).member(member).build();

		// 실증 등록
		demonstrationRegistrationRepository.save(demonstrationRegistration);
		System.out.println("저장된 Demonstration ID: " + demonstration.getDemNum());
		// 폴더에 이미지 저장 (demImages라는 폴더에)
		List<Object> files = fileUtil.saveFiles(demonstrationFormDTO.getImageList(), "demImages");

		// 이미지 파일 등록
		for (Object obj : files) {
			if (obj instanceof Map) {
				Map<String, String> map = (Map<String, String>) obj;
				DemonstrationImage demonstrationimage = DemonstrationImage.builder().imageName(map.get("originalName"))
						.imageUrl(map.get("filePath")).demonstration(Demonstration.builder().demNum(demNum).build())
						.build();
				demonstrationImageRepository.save(demonstrationimage);
			}
		}
	}

	@Override
	@Transactional
	public void updateDemonstration(DemonstrationFormDTO demonstrationFormDTO) {
		// 실증 상품 정보 업데이트
		demonstrationRepository.updateDem(demonstrationFormDTO.getDemName(), demonstrationFormDTO.getDemMfr(),
				demonstrationFormDTO.getItemNum(), demonstrationFormDTO.getDemInfo(), demonstrationFormDTO.getDemNum());

		// 반납 예정일 수정
		demonstrationRegistrationRepository.updateDemResChangeExpDate(demonstrationFormDTO.getExpDate(),
				demonstrationFormDTO.getDemNum(), demonstrationFormDTO.getMemId());

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
		if (demonstrationFormDTO.getImageList() != null && !demonstrationFormDTO.getImageList().isEmpty()) {
			List<Object> files = fileUtil.saveFiles(demonstrationFormDTO.getImageList(), "demImages");
			// 수정된 이미지로 추가
			// 안전성을 위해서 이미지 동기화 작업 고려?
			for (Object obj : files) {
				if (obj instanceof Map) {
					Map<String, String> map = (Map<String, String>) obj;
					DemonstrationImage demonstrationimage = DemonstrationImage.builder()
							.imageName(map.get("originalName")).imageUrl(map.get("filePath"))
							.demonstration(Demonstration.builder().demNum(demonstrationFormDTO.getDemNum()).build())
							.build();
					demonstrationImageRepository.save(demonstrationimage);
				}
			}
		}
	}

	// 실증 번호를 받아서 실증 상품을 삭제하는 기능
	@Override
	public void deleteDemonstration(Long demNum) {
		// 직접 DELETE 쿼리를 날리면 JPA의 영속성 컨텍스트 관리 없이 바로 DB에서 삭제가 실행
		// 즉, 엔티티의 cascade 실행하지 않아서 오류가 생김
		demonstrationRepository.deleteById(demNum);

	}

	// 실증 번호를 받아서 실증 상품의 정보를 받아오는 기능
	@Override
	public DemonstrationFormDTO selectOne(Long demNum) {
		Demonstration entity = demonstrationRepository.findById(demNum)
				.orElseThrow(() -> new RuntimeException("해당 번호의 실증 정보가 없습니다: " + demNum));
		DemonstrationFormDTO dto = modelMapper.map(entity, DemonstrationFormDTO.class);
		List<MultipartFile> imageList=demonstrationImageRepository.selectDemImageUrl(demNum);
		dto.setImageList(imageList);
		dto.setExpDate(demonstrationRegistrationRepository.selectDemRegExpDate(demNum));
		
		return dto;
	}
}
