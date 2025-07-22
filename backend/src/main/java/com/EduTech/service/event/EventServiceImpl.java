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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.entity.event.Event;
import com.EduTech.entity.event.EventState;
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
        boolean isAllowed = lowerCaseFilename.endsWith(".hwp") || lowerCaseFilename.endsWith(".pdf");
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
