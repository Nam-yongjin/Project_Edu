package com.EduTech.service.event;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventBannerDTO;
import com.EduTech.dto.event.EventFileDTO;
import com.EduTech.dto.event.EventImageDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventSearchRequestDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventBanner;
import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventFile;
import com.EduTech.entity.event.EventImage;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.event.EventUse;
import com.EduTech.entity.event.RevState;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.event.EventBannerRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventUseRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.util.FileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class EventServiceImpl implements EventService {
	
	// ========================================
    // 1. 필드 및 상수 선언
    // ========================================
	
	private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);
	private final EventBannerRepository bannerRepository;
	private final EventInfoRepository infoRepository;
	private final EventUseRepository useRepository;
	private final MemberRepository memberRepository;
	private final FileUtil fileUtil;
	private final ModelMapper modelMapper;
	
	private static final String[] WEEK_KO = { "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일" };
	
	// ========================================
    // 2. 내부 유틸리티 메서드
    // ========================================
	
	// 주 단위 계신 (숫자)
	private List<String> convertToDayNames(List<Integer> days) {
        return days.stream().map(num -> WEEK_KO[num % 7]).collect(Collectors.toList());
    }
	
	// 주 단위 계산 (한글)
	private List<LocalDate> generateClassDates(LocalDate start, LocalDate end, List<Integer> daysOfWeek) {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (daysOfWeek.contains(date.getDayOfWeek().getValue())) {
                dates.add(date);
            }
        }
        return dates;
    }
	
	// 현재 행사 신청 가능여부
	private EventState calculateState(LocalDateTime applyStartPeriod, LocalDateTime applyEndPeriod) {
	    LocalDateTime now = LocalDateTime.now();

	    if (applyStartPeriod == null || applyEndPeriod == null) {
	        return EventState.BEFORE; // 기본값 혹은 null-safe 상태로 반환
	    }

	    if (now.isBefore(applyStartPeriod)) return EventState.BEFORE;
	    else if (now.isAfter(applyEndPeriod)) return EventState.CLOSED;
	    else return EventState.OPEN;
	}
	
	// 모집대상
	private boolean isEligible(EventCategory category, Member member) {
	    if (category == null) return true; // 전체 대상
	    if (member == null || member.getRole() == null) return false;

	    return switch (category) {
	        case USER -> member.getRole() == MemberRole.USER;
	        case STUDENT -> member.getRole() == MemberRole.STUDENT;
	        case TEACHER -> member.getRole() == MemberRole.TEACHER;
	    };
	}
	
	// ========================================
    // 3. 행사 등록/수정/삭제
    // ========================================
	
	// 행사 등록
	@Override
	public void registerEvent(EventInfoDTO dto,
	                          MultipartFile mainImage,
	                          List<MultipartFile> imageList,
	                          MultipartFile mainFile,
	                          List<MultipartFile> attachList) {

	    String mainImagePath = null;
	    String mainImageOriginalName = null;
	    String mainFilePath = null;
	    String mainFileOriginalName = null;

	    // 1. 대표 이미지 저장
	    if (mainImage != null && !mainImage.isEmpty()) {
	        List<Object> results = fileUtil.saveFiles(List.of(mainImage), "Event/mainImage");
	        if (!results.isEmpty()) {
	            @SuppressWarnings("unchecked")
	            Map<String, String> result = (Map<String, String>) results.get(0);
	            mainImagePath = result.get("filePath");
	            mainImageOriginalName = result.get("originalName");
	        }
	    }

	    // 2. 대표 첨부파일 저장
	    if (mainFile != null && !mainFile.isEmpty()) {
	        List<Object> results = fileUtil.saveFiles(List.of(mainFile), "Event/mainFile");
	        if (!results.isEmpty()) {
	            @SuppressWarnings("unchecked")
	            Map<String, String> result = (Map<String, String>) results.get(0);
	            mainFilePath = result.get("filePath");
	            mainFileOriginalName = result.get("originalName");
	        }
	    }

	    // 3. DTO → Entity 변환 및 필드 세팅
	    EventInfo event = modelMapper.map(dto, EventInfo.class);
	    event.setCurrCapacity(0);
	    event.setApplyAt(LocalDateTime.now());
	    event.setState(calculateState(dto.getApplyStartPeriod(), dto.getApplyEndPeriod()));
	    event.setMainImagePath(mainImagePath);
	    event.setMainImageOriginalName(mainImageOriginalName);
	    event.setFilePath(mainFilePath);
	    event.setOriginalName(mainFileOriginalName);

	    if (event.getImageList() == null) event.setImageList(new ArrayList<>());
	    if (event.getAttachList() == null) event.setAttachList(new ArrayList<>());

	    // 4. 서브 이미지 저장
	    if (imageList != null) {
	        for (MultipartFile file : imageList) {
	            if (!file.isEmpty()) {
	                @SuppressWarnings("unchecked")
	                Map<String, String> result = (Map<String, String>) fileUtil.saveFiles(List.of(file), "Event/images").get(0);

	                EventImage image = EventImage.builder()
	                        .filePath(result.get("filePath"))
	                        .originalName(result.get("originalName"))
	                        .isMain(false)
	                        .eventInfo(event)
	                        .build();
	                event.getImageList().add(image);
	            }
	        }
	    }

	    // 5. 첨부파일 저장
	    if (attachList != null) {
	        for (MultipartFile file : attachList) {
	            if (!file.isEmpty()) {
	                @SuppressWarnings("unchecked")
	                Map<String, String> result = (Map<String, String>) fileUtil.saveFiles(List.of(file), "Event/files").get(0);

	                EventFile ef = EventFile.builder()
	                        .filePath(result.get("filePath"))
	                        .originalName(result.get("originalName"))
	                        .eventInfo(event)
	                        .build();
	                event.getAttachList().add(ef);
	            }
	        }
	    }

	    // 6. 저장
	    infoRepository.save(event);
	}
	
	// 행사 수정
	@Override
	public void updateEvent(Long eventNum,
	                        EventInfoDTO dto,
	                        MultipartFile mainImage,
	                        List<MultipartFile> imageList,
	                        MultipartFile mainFile,
	                        List<MultipartFile> attachList) {

	    EventInfo origin = infoRepository.findById(eventNum)
	            .orElseThrow(() -> new IllegalArgumentException("해당 행사가 존재하지 않습니다."));

	    if (origin.getAttachList() == null) origin.setAttachList(new ArrayList<>());
	    if (origin.getImageList() == null) origin.setImageList(new ArrayList<>());

	    dto.setEventNum(null);
	    dto.setAttachList(null);
	    dto.setImageList(null);

	    String oldImagePath = origin.getMainImagePath();
	    String oldImageOriginalName = origin.getMainImageOriginalName();
	    String oldFilePath = origin.getFilePath();
	    String oldFileName = origin.getOriginalName();

	    modelMapper.map(dto, origin);
	    origin.setCurrCapacity(dto.getCurrCapacity() != null ? dto.getCurrCapacity() : 0);
	    origin.setState(calculateState(dto.getApplyStartPeriod(), dto.getApplyEndPeriod()));

	    // 대표 이미지 수정
	    if (mainImage != null && !mainImage.isEmpty()) {
	        if (oldImagePath != null) fileUtil.deleteFiles(List.of(oldImagePath));
	        Map<String, String> fileInfo = (Map<String, String>) fileUtil.saveFiles(List.of(mainImage), "Event/mainImage").get(0);
	        origin.setMainImagePath(fileInfo.get("filePath"));
	        origin.setMainImageOriginalName(fileInfo.get("originalName"));
	    } else {
	        origin.setMainImagePath(oldImagePath);
	        origin.setMainImageOriginalName(oldImageOriginalName);
	    }

	    // 대표 파일 수정
	    if (mainFile != null && !mainFile.isEmpty()) {
	        if (oldFilePath != null) fileUtil.deleteFiles(List.of(oldFilePath));
	        Map<String, String> fileInfo = (Map<String, String>) fileUtil.saveFiles(List.of(mainFile), "Event/mainFile").get(0);
	        origin.setFilePath(fileInfo.get("filePath"));
	        origin.setOriginalName(fileInfo.get("originalName"));
	    } else {
	        origin.setFilePath(oldFilePath);
	        origin.setOriginalName(oldFileName);
	    }

	    // 서브 이미지 수정
	    if (imageList != null && !imageList.isEmpty()) {
	        List<String> deletePaths = origin.getImageList().stream()
	                .map(EventImage::getFilePath)
	                .filter(Objects::nonNull)
	                .toList();
	        fileUtil.deleteFiles(deletePaths);
	        origin.getImageList().forEach(img -> img.setEventInfo(null));
	        origin.getImageList().clear();

	        for (MultipartFile image : imageList) {
	            if (!image.isEmpty()) {
	                Map<String, String> fileInfo = (Map<String, String>) fileUtil.saveFiles(List.of(image), "Event/images").get(0);
	                EventImage img = EventImage.builder()
	                        .filePath(fileInfo.get("filePath"))
	                        .originalName(fileInfo.get("originalName"))
	                        .isMain(false)
	                        .eventInfo(origin)
	                        .build();
	                origin.getImageList().add(img);
	            }
	        }
	    }

	    // 서브 첨부파일 수정
	    if (attachList != null && !attachList.isEmpty()) {
	        for (EventFile ef : origin.getAttachList()) {
	            if (ef.getFilePath() != null) fileUtil.deleteFiles(List.of(ef.getFilePath()));
	            ef.setEventInfo(null);
	        }
	        origin.getAttachList().clear();

	        for (MultipartFile file : attachList) {
	            if (!file.isEmpty()) {
	                Map<String, String> fileInfo = (Map<String, String>) fileUtil.saveFiles(List.of(file), "Event/files").get(0);
	                EventFile ef = EventFile.builder()
	                        .filePath(fileInfo.get("filePath"))
	                        .originalName(fileInfo.get("originalName"))
	                        .eventInfo(origin)
	                        .build();
	                origin.getAttachList().add(ef);
	            }
	        }
	    }

	    infoRepository.save(origin);
	}

	// 행사 취소
	@Override
	public void deleteEvent(Long eventNum) {
	    EventInfo eventToCancel = infoRepository.findById(eventNum)
	            .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

	    // 1. 행사 상태를 CANCEL로 변경
	    eventToCancel.setState(EventState.CANCEL);

	    // 2. 신청자 상태를 CANCEL로 변경
	    List<EventUse> uses = useRepository.findByEventInfo_EventNum(eventNum);
	    for (EventUse use : uses) {
	        use.setRevState(RevState.CANCEL);
	    }

	    // 3. 변경 사항 저장
	    infoRepository.save(eventToCancel);
	    useRepository.saveAll(uses);
	}
	
	// ========================================
    // 4. 행사 조회
    // ========================================
	
	// 전체 행사 조회
	@Override
	public List<EventInfoDTO> getAllEvents() {
	    LocalDate today = LocalDate.now();
	    return infoRepository.findAll().stream()
	        .filter(info -> info.getApplyEndPeriod() != null &&
	                        !info.getApplyEndPeriod().toLocalDate().isBefore(today))
	        .map(info -> {
	            EventInfoDTO dto = modelMapper.map(info, EventInfoDTO.class);
	            return dto;
	        })
	        .collect(Collectors.toList());
	}
	
	// 현재 사용중
	@Override
	public List<EventInfoDTO> getAllEventsWithoutFilter(int page) {
	    Pageable pageable = PageRequest.of(page - 1, 8); // 0-based index
	    Page<EventInfo> result = infoRepository.findAll(pageable);

	    return result.getContent().stream()
	            .map(info -> {
	                EventInfoDTO dto = modelMapper.map(info, EventInfoDTO.class);
	                dto.setMainImagePath(info.getMainImagePath());
	                dto.setFilePath(info.getFilePath());
	                dto.setOriginalName(info.getOriginalName());
	                return dto;
	            }).toList();
	}

	
	@Override
	public Page<EventInfoDTO> searchEventList(EventSearchRequestDTO dto, int page) {
	    Sort.Direction direction = Sort.Direction.fromString(dto.getSortOrder());
	    Pageable pageable = PageRequest.of(page - 1, 8, Sort.by(direction, "applyStartPeriod"));

	    Page<EventInfo> result = infoRepository.searchEvents(dto, pageable);

	    return result.map(info -> {
	        EventInfoDTO eventDto = modelMapper.map(info, EventInfoDTO.class);
	        eventDto.setMainImagePath(info.getMainImagePath());
	        eventDto.setFilePath(info.getFilePath());
	        eventDto.setOriginalName(info.getOriginalName());
	        return eventDto;
	    });
	}
	
	
	
	
	
	
	
	// 프로그램 상세 조회
/*	@Override
	public EventInfoDTO getEvent(Long eventNum) {
		EventInfo info = infoRepository.findById(eventNum)
					.orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));
		EventInfoDTO dto = modelMapper.map(info, EventInfoDTO.class);
		dto.setOriginalName(info.getOriginalName());
		dto.setState(calculateState(info.getApplyStartPeriod(), info.getApplyEndPeriod()));
		dto.setCurrCapacity(useRepository.countByEventInfo(eventNum));
		//dto.setDayNames(convertToDayNames(info.getDaysOfWeek()));
		return dto;
	}	
*/	
	
	@Override
	public EventInfoDTO getEvent(Long eventNum) {
	    EventInfo event = infoRepository.findById(eventNum)
	            .orElseThrow(() -> new RuntimeException("해당 이벤트를 찾을 수 없습니다."));

	    // 1. 기본 매핑
	    EventInfoDTO dto = modelMapper.map(event, EventInfoDTO.class);

	    // 2. 수동 세팅 (중요 필드 누락 방지)
	    dto.setMainImagePath(event.getMainImagePath());       // 대표 이미지 경로
	    dto.setFilePath(event.getFilePath());                 // 대표 첨부파일 경로
	    dto.setOriginalName(event.getOriginalName());         // 대표 첨부파일 원본 이름

	    // 3. 이미지 리스트 변환
	    List<EventImageDTO> imageDTOs = event.getImageList().stream()
	            .map(img -> EventImageDTO.builder()
	                    .id(img.getId())
	                    .filePath(img.getFilePath())
	                    .originalName(img.getOriginalName())
	                    .build())
	            .toList();
	    dto.setImageList(imageDTOs);

	    // 4. 첨부파일 리스트 변환
	    List<EventFileDTO> fileDTOs = event.getAttachList().stream()
	            .map(f -> EventFileDTO.builder()
	                    .id(f.getId())
	                    .filePath(f.getFilePath())
	                    .originalName(f.getOriginalName())
	                    .build())
	            .toList();
	    dto.setAttachList(fileDTOs);

	    return dto;
	}
	
	
	
	
	
	
	
	@Override
    public EventInfo getEventEntity(Long eventNum) {
        return infoRepository.findById(eventNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));
    }
	
	
	// 행사 목록 조회
	@Override
	public Page<EventInfoDTO> getEventList(Pageable pageable, String eventName, String eventInfo, EventState state) {
		boolean noFilter = (eventName == null || eventName.isBlank()) && (eventInfo == null || eventInfo.isBlank());

		Page<EventInfo> result = noFilter 
				? infoRepository.findAll(pageable)
				: infoRepository.searchEvent(eventName, eventInfo, pageable);

		final String finalState = (state != null && !state.name().isBlank()) ? state.name() : null;

		List<EventInfoDTO> filteredList = result.getContent().stream().map(event -> {
			EventInfoDTO dto = modelMapper.map(event, EventInfoDTO.class);
			dto.setCurrCapacity(useRepository.countByEventInfo(event.getEventNum()));
			dto.setOriginalName(event.getOriginalName());
			EventState calculatedState = calculateState(event.getApplyStartPeriod(), event.getApplyEndPeriod());
			dto.setApplyAt(event.getApplyAt());
			dto.setState(calculatedState);
			//dto.setDayNames(convertToDayNames(event.getDaysOfWeek()));
			return dto;
		}).filter(dto -> finalState == null || finalState.equals(dto.getState())).toList();

		return new PageImpl<>(filteredList, pageable, filteredList.size());
	}
	
	
	// 사용자 검색
	@Override
    public Page<EventInfoDTO> searchEventList(Pageable pageable, String option, String query, EventState state) {
        option = (option != null && !option.isBlank()) ? option : "all";
        query = (query != null && !query.isBlank()) ? query : null;
        state = (state != null && !state.name().isBlank()) ? state : null;

        String searchType = (query != null && !query.isBlank()
                && ("eventName".equals(option) || "eventInfo".equals(option) || "all".equals(option))) ? option : null;

        Page<EventInfo> result = infoRepository.searchEvent(searchType, query, state, null, null, pageable);

        return result.map(p -> {
            EventInfoDTO dto = modelMapper.map(p, EventInfoDTO.class);
            dto.setCurrCapacity(useRepository.countByEventInfo(p.getEventNum()));
            dto.setOriginalName(p.getOriginalName());
            dto.setState(calculateState(p.getApplyStartPeriod(), p.getApplyEndPeriod()));
            //dto.setDayNames(convertToDayNames(p.getDaysOfWeek()));
            return dto;
        });
    }
	
	// 사용자의 행사 목록
	@Override
    public Page<EventInfoDTO> getUserEventList(Member member, Pageable pageable) {
        Page<EventUse> uses = useRepository.findByMember(member, pageable);
        return uses.map(use -> modelMapper.map(use.getEventInfo(), EventInfoDTO.class));
    }
	
	// 관리자 검색 목록
	@Override
    public Page<EventInfoDTO> searchAdminEventList(Pageable pageable, String option, String query, EventState state) {
        option = (option != null && !option.isBlank()) ? option : "all";
        query = (query != null && !query.isBlank()) ? query : null;
        state = (state != null && !state.name().isBlank()) ? state : null;
        String searchType = "eventName".equals(option) ? option : null;

        Page<EventInfo> result = infoRepository.searchAdminEvent(searchType, query, state, null, null, pageable);

        return result.map(p -> {
        	EventInfoDTO dto = modelMapper.map(p, EventInfoDTO.class);
            dto.setCurrCapacity(useRepository.countByEventInfo(p.getEventNum()));
            dto.setOriginalName(p.getOriginalName());
            dto.setState(calculateState(p.getApplyStartPeriod(), p.getApplyEndPeriod()));
            //dto.setDayNames(convertToDayNames(p.getDaysOfWeek()));
            return dto;
        });
    }
	
	// 관리자용: 특정 프로그램의 신청자 목록 조회
	@Override
	public List<EventUseDTO> getApplicantsByEvent(Long eventNum) {
		List<EventUse> list = useRepository.findByEventInfo_EventNum(eventNum);
		return list.stream().map(this::toDTO).collect(Collectors.toList());
	}
	
	// 사용자 신청 리스트
	// 회원 ID(mid)를 기준으로 해당 회원이 신청한 프로그램 목록을 페이지 형태로 조회
	@Override
	public Page<EventUseDTO> getUseListByMemberPaged(String memId, Pageable pageable) {
	    if (memId == null || memId.isBlank()) {
	        throw new IllegalArgumentException("회원 ID는 null 또는 빈 값일 수 없습니다.");
	    }

	    Page<EventUse> result = useRepository.findByMember_MemId(memId, pageable);

	    return result.map(this::toDTO); // 또는: result.map(eventUseMapper::toDTO);
	}
	
	// 진행 중인 이벤트만 조회
	@Override
    public List<EventInfoDTO> searchNotEndEventList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "applyAt");
        List<EventInfo> infoList = infoRepository.findByEventEndPeriodGreaterThanEqual(LocalDateTime.now(), sort);
        return infoList.stream().map(p -> {
            EventInfoDTO dto = modelMapper.map(p, EventInfoDTO.class);
            dto.setCurrCapacity(useRepository.countByEventInfo(p.getEventNum()));
            return dto;
        }).collect(Collectors.toList());
    }
	
	// ========================================
    // 5. 배너 기능
    // ========================================
	
	// 배너 등록
	@Override
	public void registerBanner(EventBannerDTO dto) {
	    // 1. 이미 등록된 행사인지 확인 먼저!
	    if (bannerRepository.existsByEventInfo_EventNum(dto.getEventNum())) {
	        throw new IllegalStateException("해당 프로그램에는 이미 배너가 등록되어 있습니다.");
	    }

	    // 2. 최대 3개 제한 체크는 그 다음
	    LocalDateTime today = LocalDateTime.now();
	    long currentBannerCount = bannerRepository.countValidBanners(today);
	    if (currentBannerCount >= 3) {
	        throw new IllegalStateException("배너는 최대 3개까지 등록할 수 있습니다.");
	    }

	    // 3. 행사 정보 가져오기
	    EventInfo event = infoRepository.findById(dto.getEventNum())
	            .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

	    // 4. 대표 이미지가 없는 경우 기본 이미지로 설정
	    String defaultImagePath = "default/no-image.png";
	    String defaultOriginalName = "기본이미지";

	    EventBanner banner = new EventBanner();
	    banner.setOriginalName(
	        event.getMainImageOriginalName() != null ? event.getMainImageOriginalName() : defaultOriginalName
	    );
	    banner.setFilePath(
	        event.getMainImagePath() != null ? event.getMainImagePath() : defaultImagePath
	    );
	    banner.setEventInfo(event);

	    bannerRepository.save(banner);
	}
	
	// 배너 삭제
	@Override
    public void deleteBanner(Long evtFileNum) {
		EventBanner banner = bannerRepository.findById(evtFileNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 배너가 존재하지 않습니다."));
        String filePath = banner.getFilePath();
        if (filePath != null) {
            String fileName = Paths.get(filePath).getFileName().toString();
            String parent = Paths.get(filePath).getParent().toString();
            String thumbnailPath = parent + "/s_" + fileName;
            fileUtil.deleteFiles(List.of(filePath, thumbnailPath));
        }
        bannerRepository.delete(banner);
    }
	
	// 배너 조회 리스트
	@Override
	public List<EventInfoDTO> getAllBanners(int page) {
	    Pageable pageable = PageRequest.of(page - 1, 8); // 0-based index
	    Page<EventInfo> result = infoRepository.findAll(pageable);

	    return result.getContent().stream()
	            .map(info -> {
	                EventInfoDTO dto = modelMapper.map(info, EventInfoDTO.class);
	                dto.setMainImagePath(info.getMainImagePath());
	                dto.setFilePath(info.getFilePath());
	                dto.setOriginalName(info.getOriginalName());
	                return dto;
	            }).toList();
	}
	
	// ========================================
	// 6. 사용자 신청/취소/중복확인
	// ========================================
	
	// 사용자 행사 신청
	@Override
	@Transactional
	public void applyEvent(EventApplyRequestDTO dto) {
	    Long eventNum = dto.getEventNum();
	    String memId = dto.getMemId();

	    if (memId == null || memId.isBlank()) {
	        throw new IllegalStateException("로그인한 사용자만 신청할 수 있습니다.");
	    }

	    if (isAlreadyApplied(eventNum, memId)) {
	        throw new IllegalStateException("이미 신청한 프로그램입니다.");
	    }

	    EventInfo event = infoRepository.findById(eventNum)
	            .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

	    LocalDateTime now = LocalDateTime.now();

	    if (event.getApplyStartPeriod() == null || event.getApplyEndPeriod() == null) {
	        throw new IllegalStateException("신청 기간 정보가 없습니다.");
	    }

	    if (now.isBefore(event.getApplyStartPeriod())) {
	        throw new IllegalStateException("신청 기간이 아닙니다.");
	    }
	    if (now.isAfter(event.getApplyEndPeriod())) {
	        throw new IllegalStateException("신청 기간이 종료되었습니다.");
	    }

	    if (event.getCurrCapacity() >= event.getMaxCapacity()) {
	        throw new IllegalStateException("모집 정원이 초과되었습니다.");
	    }

	    Member member = memberRepository.findById(memId)
	            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

	    if (member.getState() == MemberState.BEN) {
	        throw new IllegalStateException("회원이 정지 상태로 인해 프로그램을 신청할 수 없습니다.");
	    }

	    if (member.getState() == MemberState.LEAVE) {
	        throw new IllegalStateException("탈퇴한 계정은 신청할 수 없습니다.");
	    }

	    // USER는 누구나 신청 가능
	    if (event.getCategory() != EventCategory.USER && !isEligible(event.getCategory(), member)) {
	        throw new IllegalStateException("신청 대상이 아닙니다.");
	    }

	    // ✅ 바로 승인 상태로 저장
	    EventUse eventUse = EventUse.builder()
	            .eventInfo(event)
	            .member(member)
	            .revState(RevState.APPROVED)  // 즉시 승인
	            .applyAt(LocalDateTime.now())
	            .build();

	    useRepository.save(eventUse);

	    // 신청과 동시에 현재 인원 증가
	    event.increaseCurrCapacity();
	}
	
	// 행사 신청 취소
	@Override
	@Transactional
	public void cancelEvent(Long evtRevNum, String memId) {
	    EventUse eventUse = useRepository.findById(evtRevNum)
	            .orElseThrow(() -> new IllegalArgumentException("신청 내역이 존재하지 않습니다."));

	    if (!eventUse.getMember().getMemId().equals(memId)) {
	        throw new AccessDeniedException("예약 취소 권한이 없습니다.");
	    }

	    // 상태를 CANCEL로 변경
	    eventUse.setRevState(RevState.CANCEL);
	}
	
	// 행사 신청 여부 확인
	@Override
	public boolean isAlreadyApplied(Long eventNum, String memIid) {
	    return useRepository.existsByEventInfo_EventNumAndMember_MemId(eventNum, memIid);
	}
	
	// 행사 신청 가능 여부(화면단 버튼 비활성화용)
	@Override
	public boolean isAvailable(Long eventNum) {
	    EventInfo event = infoRepository.findById(eventNum)
	            .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

	    LocalDateTime end = event.getApplyEndPeriod();
	    return end != null && end.toLocalDate().isAfter(LocalDate.now());
	}
	
	// ========================================
	// 7. 공통 메서드
	// ========================================
	public EventUseDTO toDTO(EventUse use) {
	    EventInfo info = use.getEventInfo();
	    Member member = use.getMember();

	    String state = info.getEventEndPeriod().isBefore(LocalDate.now().atStartOfDay()) ? "행사종료" : "신청완료";

//	    String eventStartPeriod = info.getEventStartPeriod() != null ? info.getEventStartPeriod().toString() : "";
//	    String eventEndPeriod = info.getEventEndPeriod() != null ? info.getEventEndPeriod().toString() : "";

	    return EventUseDTO.builder()
	            .evtRevNum(use.getEvtRevNum())
	            .eventNum(info.getEventNum())
	            .eventName(info.getEventName())
	            .eventStartPeriod(info.getEventStartPeriod())
	            .eventEndPeriod(info.getEventEndPeriod())
	            .place(info.getPlace())
	            .maxCapacity(info.getMaxCapacity())
	            .currCapacity(useRepository.countByEventInfo(info.getEventNum()))
	            .revState(use.getRevState())
	            .applyAt(use.getApplyAt())
	            .memId(member != null ? member.getMemId() : null)
	            .name(member != null ? member.getName() : null)
	            .email(member != null ? member.getEmail() : null)
	            .phone(member != null ? member.getPhone() : null)
	            .build();
	}

	public void setFileInfo(EventInfo info, MultipartFile file) {
	    if (file != null && !file.isEmpty()) {
	        String originalFilename = file.getOriginalFilename();

	        if (originalFilename == null || originalFilename.isEmpty()) {
	            throw new IllegalArgumentException("파일 이름이 존재하지 않습니다.");
	        }

	        String lowerCaseFilename = originalFilename.toLowerCase();

	        // 허용 확장자 목록
	        List<String> allowedExtensions = List.of(".hwp", ".pdf", ".jpg", ".jpeg", ".png", ".gif", ".webp");

	        boolean isAllowed = allowedExtensions.stream().anyMatch(lowerCaseFilename::endsWith);
	        if (!isAllowed) {
	            throw new IllegalArgumentException("허용된 파일 형식(hwp, pdf, 이미지 파일)만 업로드 가능합니다.");
	        }

	        // 기존 파일 삭제
	        String oldPath = info.getFilePath();
	        if (oldPath != null && !oldPath.isEmpty()) {
	            fileUtil.deleteFiles(List.of(oldPath));
	        }

	        // 새 파일 저장
	        List<Object> uploaded = fileUtil.saveFiles(List.of(file), "event");
	        if (!uploaded.isEmpty()) {
	            @SuppressWarnings("unchecked")
	            Map<String, String> fileInfoMap = (Map<String, String>) uploaded.get(0);
	            info.setOriginalName(fileInfoMap.get("originalName"));
	            info.setFilePath(fileInfoMap.get("filePath"));
	        }
	    }
	}
	
}
