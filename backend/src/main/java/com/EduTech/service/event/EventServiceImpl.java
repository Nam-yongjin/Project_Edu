package com.EduTech.service.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.Event;
import com.EduTech.entity.event.EventFile;
import com.EduTech.entity.event.EventReserve;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.member.Member;
import com.EduTech.repository.event.EventBannerRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventReserveRepository;
import com.EduTech.util.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
	
	private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);
	
	private final EventBannerRepository bannerRepository;
	private final EventInfoRepository infoRepository;
	private final EventReserveRepository reserveRepository;
	private final FileUtil fileUtil;
	private final ModelMapper modelMapper;
	
	private static final String[] WEEK_KO = { "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일" };
	
	// 요일 숫자를 한글 요일명으로 변경
	private List<String> convertToDayNames(List<Integer> days) {
		return days.stream().map(num -> WEEK_KO[num % 7]).collect(Collectors.toList());
	}
	
	// 현재 시간 기준으로 신청 상태 판단
    private EventState calculateStatus(LocalDateTime applyStartPeriod, LocalDateTime applyEndPeriod) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(applyStartPeriod)) {
            return EventState.신청전;
        } else if (now.isAfter(applyEndPeriod)) {
            return EventState.신청마감;
        } else {
            return EventState.신청중;
        }
    }
	
	// 행사 날짜 생성 메서드 (요일 포함된 실제 수업일 계산)
    private List<LocalDate> generateClassDates(LocalDate start, LocalDate end, List<Integer> daysOfWeek) {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            int day = date.getDayOfWeek().getValue() % 7;
            if (daysOfWeek.contains(day)) {
                dates.add(date);
            }
        }
        return dates;
    }
    
    // 필수 입력값 검증
    private void validateDto(EventInfoDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("행사 정보가 존재하지 않습니다.");
        }
        if (dto.getDaysOfWeek() == null || dto.getDaysOfWeek().isEmpty()) {
            throw new IllegalArgumentException("요일 정보가 누락되었습니다.");
        }
    }
	
	
 // 행사 등록
    @Override
    public void registerEvent(EventInfoDTO dto, MultipartFile file) {
        validateDto(dto);
        Event info = modelMapper.map(dto, Event.class);
        info.setApplyAt(LocalDateTime.now());
        info.setState(calculateStatus(dto.getApplyStartPeriod(), dto.getApplyEndPeriod()));
        info.setDaysOfWeek(dto.getDaysOfWeek());
        setFileInfo(info, file);
        infoRepository.save(info);
    }
	
    // 행사 수정
    @Override
    public void updateEvent(Long eventNum, EventInfoDTO dto, MultipartFile file) {
        validateDto(dto);

        Event origin = infoRepository.findById(eventNum)
            .orElseThrow(() -> new IllegalArgumentException("해당 프로그램이 존재하지 않습니다."));

        String originalFilePath = origin.getFilePath();
        String originalFileName = origin.getOriginalName();
        LocalDateTime originalApplyAt = origin.getApplyAt();

        modelMapper.map(dto, origin);
        origin.setApplyAt(originalApplyAt);

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
    
    // 행사 삭제 처리
    @Override
    public void deleteEvent(Long eventNum) {
        Event event = infoRepository.findById(eventNum)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 행사입니다."));

        // 신청자 정보 삭제
        List<EventReserve> reserves = reserveRepository.findByEvent_eventNum(eventNum);
        reserveRepository.deleteAll(reserves);

        // 배너 삭제
        EventFile banner = bannerRepository.findByEvent_eventNum(eventNum).orElse(null);
        if (banner != null) {
            List<String> files = new ArrayList<>();
            files.add(banner.getFilePath());
            fileUtil.deleteFiles(files);
            bannerRepository.delete(banner);
        }

        // 첨부파일 삭제
        List<EventFile> files = event.getEventFiles();
        List<String> filePaths = new ArrayList<>();
        for (EventFile ef : files) {
            filePaths.add(ef.getFilePath());
        }
        fileUtil.deleteFiles(filePaths);

        // 행사 삭제
        infoRepository.delete(event);
    }
    
    // 관리자 일반 목록 (복합 필터 포함)
    @Override
    public Page<EventInfoDTO> searchAdminEventList(Pageable pageable, String name, String content, String status) {
        Page<Event> result;
        if (name == null && content == null) {
            result = infoRepository.findAll(pageable);
        } else {
            result = infoRepository.searchEvent(name, content, pageable);
        }

        List<EventInfoDTO> list = new ArrayList<>();
        for (Event event : result.getContent()) {
            EventInfoDTO dto = modelMapper.map(event, EventInfoDTO.class);
            dto.setCurrCapacity(event.getCurrCapacity());
            dto.setRevState(event.getState().name());
            list.add(dto);
        }

        return new PageImpl<>(list, pageable, result.getTotalElements());
    }   
    
    // 사용자 검색용 행사 리스트
    @Override
    public Page<EventInfoDTO> searchEventList(Pageable pageable, String option, String keyword, String status) {
        Page<Event> events = infoRepository.searchEvent(option, keyword, pageable);
        List<EventInfoDTO> result = new ArrayList<>();

        for (Event event : events.getContent()) {
            EventInfoDTO dto = modelMapper.map(event, EventInfoDTO.class);
            dto.setCurrCapacity(event.getCurrCapacity());
            dto.setRevState(event.getState().name());
            result.add(dto);
        }

        return new PageImpl<>(result, pageable, events.getTotalElements());
    }    
    
    // 페이징 형태로 조회
    @Override
	public Page<EventInfoDTO> getUserEventList(Member member, Pageable pageable) {
		// 회원이 신청한 EventReserveRepository 목록을 기준으로 EventInfoDTO 가져오기
		Page<EventReserveRepository> uses = useRepository.findByMember(member, pageable);
		Page<EventInfoDTO> dtoPage = uses.map(use -> modelMapper.map(use.getEventInfoDTO(), EventInfoDTO.class));

		return dtoPage;
	}
    
    
    
    
    
    // ------------------------ 공통 메서드 ------------------------------
    
    // ProgramUse → ProgramUseDTO 변환 메서드
 	private EventUseDTO toDTO(EventReserve use) {
 		Event info = use.getEvent();
 		Member member = use.getMember();

 		String status = info.getApplyEndPeriod().isBefore(LocalDateTime.now()) ? "신청마감" : "신청완료";
 		List<Integer> dayOfWeekIntList = info.getDaysOfWeek();

 		String startTime = info.getStartTime() != null ? info.getStartTime().toString() : "";
 		String endTime = info.getEndTime() != null ? info.getEndTime().toString() : "";

 		return EventUseDTO.builder().evtRevNum(use.getEvtRevNum()).applyAt(use.getApplyAt()).eventNum(info.getEventNum())
 				.eventName(info.getEventName()).progressStartPeriod(info.getProgressStartPeriod())
 				.progressEndPeriod(info.getProgressEndPeriod()).LocalTime(startTime).LocalTime(endTime).daysOfWeek(dayOfWeekIntList)
 				.room(info.getPlace()).capacity(info.getCurrCapacity())
 				.current(reserveRepository.countByEvent(info.getEventNum())).status(status)
 				.mid(member != null ? member.getMemId() : null).name(member != null ? member.getName() : null)
 				.email(member != null ? member.getEmail() : null).phone(member != null ? member.getPhone() : null)
 				.build();
 	}
    
    
		
    // 파일 저장 및 유효성 검사
    private void setFileInfo(Event info, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.info("파일이 전달되지 않음 → 기존 파일 유지");
            return;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 존재하지 않습니다.");
        }

        String lowerCaseFilename = originalFilename.toLowerCase();
        boolean isAllowed = lowerCaseFilename.endsWith(".hwp") || lowerCaseFilename.endsWith(".pdf") || lowerCaseFilename.endsWith(".jpg");
        if (!isAllowed) {
            throw new IllegalArgumentException("hwp 또는 pdf 파일만 업로드 가능합니다.");
        }

        String oldPath = info.getFilePath();
        if (oldPath != null && !oldPath.isEmpty()) {
            fileUtil.deleteFiles(List.of(oldPath));
        }

        List<Object> uploaded = fileUtil.saveFiles(List.of(file), "program");
        if (!uploaded.isEmpty()) {
            @SuppressWarnings("unchecked")
            Map<String, String> fileInfoMap = (Map<String, String>) uploaded.get(0);
            info.setOriginalName(fileInfoMap.get("originalName"));
            info.setFilePath(fileInfoMap.get("filePath"));
            log.info("파일 저장 완료 - 이름: {}, 경로: {}", fileInfoMap.get("originalName"), fileInfoMap.get("filePath"));
        }
    }
	
	
}
