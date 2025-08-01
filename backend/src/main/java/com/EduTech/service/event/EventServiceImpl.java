package com.EduTech.service.event;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventBannerDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventBanner;
import com.EduTech.entity.event.EventCategory;
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
	public void registerEvent(EventInfoDTO dto, MultipartFile file) {
	    EventInfo info = modelMapper.map(dto, EventInfo.class);

	    // 누락 필드 보정
	    info.setCurrCapacity(0); // 새 등록 이벤트는 현재 인원 0

	    // null 방지: daysOfWeek는 @ElementCollection이므로 null이면 오류 발생 가능
	    if (info.getDaysOfWeek() == null) {
	        info.setDaysOfWeek(new ArrayList<>());
	    }
	    
	    // 작성시간
	    info.setApplyAt(LocalDateTime.now());

	    // 상태 계산
	    info.setState(calculateState(dto.getApplyStartPeriod(), dto.getApplyEndPeriod()));

	    // 파일 처리
	    setFileInfo(info, file);

	    // 저장
	    infoRepository.save(info);
	}

	
	// 행사 수정
	/*
	@Override
    public void updateEvent(Long eventNum, EventInfoDTO dto, MultipartFile file) {
        EventInfo origin = infoRepository.findById(eventNum)
        		.orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

        String originalFilePath = origin.getFilePath();
        String originalFileName = origin.getOriginalName();

        modelMapper.map(dto, origin);

        if (file != null && !file.isEmpty()) {
            if (originalFilePath != null && !originalFilePath.isEmpty()) {
                fileUtil.deleteFiles(List.of(originalFilePath));
            }
            setFileInfo(origin, file);
        } else {
            origin.setFilePath(dto.getFilePath() != null ? dto.getFilePath() : originalFilePath);
            origin.setOriginalName(dto.getOriginalName() != null ? dto.getOriginalName() : originalFileName);
        }

        infoRepository.save(origin);
    }
	*/
	@Override
	public void updateEvent(Long eventNum, EventInfoDTO dto, MultipartFile file) {
	    EventInfo origin = infoRepository.findById(eventNum)
	            .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

	    // ⚠ ID 필드 덮어쓰기 방지를 위해 null로 설정하거나 수동 매핑
	    dto.setEventNum(null); // ID를 덮어쓰지 않도록 방지

	    String originalFilePath = origin.getFilePath();
	    String originalFileName = origin.getOriginalName();

	 // ★ 여기서 ID를 건드리지 않게 modelMapper 설정되어 있어야 함
	    modelMapper.map(dto, origin);

	    // 파일 업로드 처리
	    if (file != null && !file.isEmpty()) {
	        if (originalFilePath != null && !originalFilePath.isEmpty()) {
	            fileUtil.deleteFiles(List.of(originalFilePath));
	        }
	        setFileInfo(origin, file);
	    } else {
	        origin.setFilePath(dto.getFilePath() != null ? dto.getFilePath() : originalFilePath);
	        origin.setOriginalName(dto.getOriginalName() != null ? dto.getOriginalName() : originalFileName);
	    }

	    infoRepository.save(origin);
	}
	
	// 행사 삭제
	@Override
    public void deleteEvent(Long eventNum) {
        EventInfo eventToDelete = infoRepository.findById(eventNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

        bannerRepository.findByEventInfo_EventNum(eventNum).ifPresent(banner -> {
            String filePath = banner.getFilePath();
            if (filePath != null) {
                String fileName = Paths.get(filePath).getFileName().toString();
                String parent = Paths.get(filePath).getParent().toString();
                String thumbnailPath = parent + "/s_" + fileName;
                fileUtil.deleteFiles(List.of(filePath, thumbnailPath));
            }
            bannerRepository.delete(banner);
        });

        List<EventUse> uses = useRepository.findByEventInfo_EventNum(eventNum);
        useRepository.deleteAll(uses);

        if (eventToDelete.getFilePath() != null && !eventToDelete.getFilePath().isEmpty()) {
            try {
                fileUtil.deleteFiles(List.of(eventToDelete.getFilePath()));
            } catch (RuntimeException e) {
                throw new RuntimeException("파일 삭제 중 문제가 발생했습니다. 관리자에게 문의해주세요.");
            }
        }

        infoRepository.delete(eventToDelete);
    }
	
	// ========================================
    // 4. 행사 조회
    // ========================================
	
	// 전체 행사 조회
	@Override
	public List<EventInfoDTO> getAllEvents() {
		LocalDate today = LocalDate.now();
		return infoRepository.findAll().stream().filter(info -> !info.getApplyEndPeriod().toLocalDate().isBefore(today))
				.map(info -> {
					EventInfoDTO dto = modelMapper.map(info, EventInfoDTO.class);
					//dto.setDayNames(convertToDayNames(info.getDaysOfWeek()));
					return dto;
				}).collect(Collectors.toList());
	}
	
	// 프로그램 상세 조회
	@Override
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
		Page<EventUse> result = useRepository.findByMember_MemId(memId, pageable);

		return result.map(this::toDTO);
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
    public void registerBanner(EventBannerDTO dto, MultipartFile file) {
		LocalDateTime today = LocalDateTime.now();
        long currentBannerCount = bannerRepository.countValidBanners(today);
        if (currentBannerCount >= 9) {
            throw new IllegalStateException("배너는 최대 3개까지 등록할 수 있습니다.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("배너 이미지를 첨부해주세요.");
        }
        if (!file.getContentType().startsWith("image")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        if (bannerRepository.existsByEventInfo_EventNum(dto.getEventInfoId())) {
            throw new IllegalStateException("해당 프로그램에는 이미 배너가 등록되어 있습니다.");
        }

        List<Object> savedFiles = fileUtil.saveFiles(List.of(file), "Event/banner");
        Map<String, String> fileMap = (Map<String, String>) savedFiles.get(0);

        EventInfo event = infoRepository.findById(dto.getEventInfoId())
                .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

        EventBanner banner = new EventBanner();
        banner.setOriginalName(fileMap.get("originalName"));
        banner.setFilePath(fileMap.get("filePath"));
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
	public List<EventBannerDTO> getAllBanners() {
		LocalDateTime today = LocalDateTime.now();
		List<EventBanner> result = bannerRepository.findValidBanners(today);

		return result.stream().map(banner -> {
			EventInfo info = banner.getEventInfo();
			EventBannerDTO dto = new EventBannerDTO();
			dto.setEvtFileNum(banner.getEvtFileNum());
			dto.setOriginalName(banner.getOriginalName());
			dto.setFilePath(banner.getFilePath());
			dto.setEventInfoId(info.getEventNum());

			// 썸네일 경로 생성 (s_ 접두사 방식)
			String filePath = banner.getFilePath();
			if (filePath != null && filePath.contains("/")) {
				String fileName = Paths.get(filePath).getFileName().toString();
				String parent = Paths.get(filePath).getParent().toString();
				dto.setThumbnailPath(filePath);
			}
			
			// 프로그램 정보 추가
			dto.setEventName(info.getEventName());
			dto.setCategory(info.getCategory());
			dto.setEventStartPeriod(info.getEventStartPeriod());
			dto.setEventEndPeriod(info.getEventEndPeriod());
			dto.setDaysOfWeek(info.getDaysOfWeek());
			dto.setDayNames(convertToDayNames(info.getDaysOfWeek()));

			return dto;
		}).collect(Collectors.toList());
	}
	
	// ========================================
	// 6. 사용자 신청/취소/중복확인
	// ========================================
	
	// 사용자 행사 신청
	@Override
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

	    if (now.isBefore(event.getApplyStartPeriod())) {
	        throw new IllegalStateException("신청 기간이 아닙니다.");
	    }
	    if (now.isAfter(event.getApplyEndPeriod())) {
	        throw new IllegalStateException("신청 기간이 종료되었습니다.");
	    }

	    Member member = memberRepository.findById(memId)
	            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

	    if (member.getState() == MemberState.BEN) {
	        throw new IllegalStateException("회원이 정지 상태로 인해 프로그램을 신청할 수 없습니다.");
	    }
	    if (member.getState() == MemberState.LEAVE) {
	        throw new IllegalStateException("탈퇴한 계정은 신청할 수 없습니다.");
	    }

	    if (!isEligible(event.getCategory(), member)) {
	        throw new IllegalStateException("신청 대상이 아닙니다.");
	    }
	    
	    if (dto.getRevState() == null) {
	        dto.setRevState(RevState.WAITING); // 기본 상태로 보완
	    }

	    EventUse eventUse = EventUse.builder()
	            .eventInfo(event)
	            .member(member)
	            .revState(dto.getRevState())
	            .applyAt(LocalDateTime.now())
	            .build();

	    try {
	        useRepository.save(eventUse);
	    } catch (DataIntegrityViolationException e) {
	        log.warn("중복 신청 시도 감지 - eventNum={}, memId={}", eventNum, memId);
	        throw new IllegalStateException("이미 신청한 프로그램입니다.");
	    }
	}
	
	// 행사 신청 취소
	@Override
	public void cancelEvent(Long evtRevNum) {
	    EventUse eventUse = useRepository.findById(evtRevNum)
	            .orElseThrow(() -> new IllegalArgumentException("신청 내역이 존재하지 않습니다."));
	    useRepository.delete(eventUse);
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
	    return event.getApplyEndPeriod().toLocalDate().isAfter(LocalDate.now());
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
	        boolean isAllowedDocument = lowerCaseFilename.endsWith(".hwp") || lowerCaseFilename.endsWith(".pdf");

	        if (!isAllowedDocument) {
	            throw new IllegalArgumentException("hwp 또는 pdf 파일만 업로드 가능합니다.");
	        }

	        String oldPath = info.getFilePath();
	        if (oldPath != null && !oldPath.isEmpty()) {
	            fileUtil.deleteFiles(List.of(oldPath));
	        }

	        List<Object> uploaded = fileUtil.saveFiles(List.of(file), "event");
	        if (!uploaded.isEmpty()) {
	            @SuppressWarnings("unchecked")
	            Map<String, String> fileInfoMap = (Map<String, String>) uploaded.get(0);
	            info.setOriginalName(fileInfoMap.get("originalName"));
	            info.setFilePath(fileInfoMap.get("filePath"));
	        }
	    }
	}
	
	
	// 내너의 경우에는 위의 코드를 사용 못할수도 있으므오 다른 코드를 작성해야 할수도 있음 나중에 확인 해봄
	
	
	
	// 행사 목록 조회
/*
	public Page<EventInfoDTO> getEventList(Pageable pageable, String eventName, String content, String state) {
        boolean noFilter = (eventName == null || eventName.isBlank()) && (content == null || content.isBlank());

        Page<EventInfo> result = noFilter
                ? infoRepository.findAll(pageable)
                : infoRepository.searchEvent(eventName, content, pageable);

        final String finalState = (state != null && !state.isBlank()) ? state : null;

        List<EventInfoDTO> filteredList = result.getContent().stream().map(event -> {
            EventInfoDTO dto = modelMapper.map(event, EventInfoDTO.class);
            dto.setCurrCapacity(useRepository.countByEventInfo(event.getEventNum()));
            dto.setOriginalName(event.getOriginalName());
            dto.setState(calculateState(event.getApplyStartPeriod(), event.getApplyEndPeriod()));
            dto.setDayNames(convertToDayNames(event.getDaysOfWeek()));
            return dto;
        }).filter(dto -> finalState == null || finalState.equals(dto.getState())).toList();

        return new PageImpl<>(filteredList, pageable, filteredList.size());
    }
*/
	
}
